package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.*;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.OrderEntityRepository;
import com.pizzacalculator.pizzacalculatorserverside.security.exception.IllegalOperationException;
import com.pizzacalculator.pizzacalculatorserverside.security.exception.NotFoundException;
import com.pizzacalculator.pizzacalculatorserverside.security.model.User;
import com.pizzacalculator.pizzacalculatorserverside.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CartService {
    Logger logger = Logger.getLogger(this.getClass().toString());
    public final OrderService service;
    private final UserRepository userRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final KitchenService kitchen;

    public CartResponse getCart(Long clientId) {
        var user = getUser(clientId);
        return getCartResponse(user.getOrders());
    }

    private User getUser(Long clientId) {
        return userRepository.findById(clientId).orElseThrow(() -> new NotFoundException("Not found client with id: " + clientId));
    }

    private List<OrderInCart> notFinishedOrdersFromEntity(List<OrderEntity> orders) {
        return orders.stream().filter(x -> x.getState().ordinal() < OrderState.ORDER_DONE.ordinal())
                .sorted(Comparator.comparing(OrderEntity::getTime).reversed())
                .map(OrderEntity::getDTO)
                .toList();
    }

    private BigDecimal sum(List<OrderInCart> orderRequests) {
        if (orderRequests.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderRequests.stream()
                .filter(x -> !x.isPaid())
                .map(x -> x.getPrice().multiply(BigDecimal.valueOf(x.getAmount())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public CartResponse changeAmount(ChangeAmountRequest body, long clientId) {
        if (body == null || body.orderId() == null || body.isPositiveChange() == null) {
            throw new IllegalOperationException("Request body or some of the fields are null");
        }
        if (body.orderId() <= 0) {
            throw new IllegalOperationException("Id can't be less or equal to 0");
        }
        var user = getUser(clientId);
        var orders = user.getOrders();
        int diff = body.isPositiveChange() ? 1 : -1;
        var find = orders.stream()
                .filter(x -> !x.getPaid())
                .filter(x -> Objects.equals(x.getId(), body.orderId()))
                .findAny();
        find.ifPresent(order -> {
            if (order.getAmount() <= 1 && diff < 0) {
                orders.remove(order);
                orderEntityRepository.delete(order);
                userRepository.save(user);
            } else if (order.getAmount() >= 1) {
                order.changeAmount(diff);
                orderEntityRepository.save(order);
            }
        });
        return getCartResponse(orders);
    }

    @NotNull
    private CartResponse getCartResponse(List<OrderEntity> orders) {
        List<OrderInCart> dtoOrders = notFinishedOrdersFromEntity(orders);
        CartResponse cartResponse = new CartResponse(sum(dtoOrders), dtoOrders);
        logger.info(cartResponse.toString());
        return cartResponse;
    }

    public CartResponse deleteOrder(Long clientId, Long orderId) {
        var user = getUser(clientId);
        List<OrderEntity> orders = user.getOrders();
        Optional<OrderEntity> order = orders.stream().filter(x -> Objects.equals(x.getId(), orderId)).findAny();
        order.ifPresentOrElse(o -> {
            if (o.getPaid()) {
                throw new IllegalOperationException("You can't modify paid order: " + o);
            } else {
                orders.remove(o);
                orderEntityRepository.delete(o);
                userRepository.save(user);
            }
        }, () -> {
            throw new NotFoundException("Not found order to delete with id: " + orderId);
        });
        return getCartResponse(orders);
    }

    public TotalPrice calculateDelivery(Long clientId, Address address) {
        var user = getUser(clientId);
        BigDecimal sum = sum(notFinishedOrdersFromEntity(user.getOrders()));
        return calculateDeliveryResponse(address, sum);
    }

    private static final String homeCity = "rivne";
    private static final BigDecimal deliveryCoeffForHome = BigDecimal.valueOf(0.05);
    private static final BigDecimal deliveryCoeffForOtherCities = BigDecimal.valueOf(0.15);
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING_MODE = RoundingMode.HALF_UP;

    private static TotalPrice calculateDeliveryResponse(Address address, BigDecimal sum) {
        final BigDecimal deliveryCoeff = address.city().toLowerCase()
                .contains(homeCity) ? deliveryCoeffForHome : deliveryCoeffForOtherCities;
        BigDecimal delivery = sum.multiply(deliveryCoeff).setScale(MONEY_SCALE, MONEY_ROUNDING_MODE);
        return new TotalPrice(sum, sum.add(delivery));
    }

    public CartResponse checkoutWithDelivery(Long clientId, boolean delivery) {
        var user = getUser(clientId);
        List<OrderEntity> orders = user.getOrders();
        List<OrderEntity> listToKitchen = orders.stream().filter(o -> !o.getPaid()).toList();
        listToKitchen.forEach(x -> {
            x.setPaid(true);
            x.setState(OrderState.COOKING);
        });
        orderEntityRepository.saveAll(listToKitchen);
        kitchen.submitTasks(user.getId(), listToKitchen, delivery);
        return new CartResponse(BigDecimal.ZERO, notFinishedOrdersFromEntity(orders));
    }

    public CartResponse checkoutCarryOut(Long clientId) {
        return checkoutWithDelivery(clientId, false);
    }

    public TimeToWaitResponse checkStatus(Long clientId) {
        CartResponse cartResponse = getCart(clientId);
        long seconds = kitchen.getTimeToWait(clientId);
        return new TimeToWaitResponse(seconds, cartResponse);
    }
}
