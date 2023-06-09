package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderInCart;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderRequest;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderResponse;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderState;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderDetailsEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingCounter;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.OrderDetailsRepository;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.OrderEntityRepository;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.ToppingRepository;
import com.pizzacalculator.pizzacalculatorserverside.security.exception.NotFoundException;
import com.pizzacalculator.pizzacalculatorserverside.security.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class OrderService {

    public static final Map<Long, List<OrderInCart>> map = new HashMap<>();

    private final ToppingRepository repository;
    private final UserRepository userRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public OrderResponse addOrderToCart(OrderRequest order, Long id) {
        var user = userRepository.findById(id).orElseThrow(()->new NotFoundException("not found user: " + id));
        OrderEntity o = orderToEntity(order);
        orderEntityRepository.save(o);
        logger.info("OrderEntity o: " + o);
        user.addOrder(o);
        logger.info("user.addOrder: " + o);
        userRepository.save(user);
        return new OrderResponse(o.getId(), calculateCartSum(user.getOrders()));
    }

    public BigDecimal calculateCartSum(List<OrderEntity> orders) {
        return orders.stream().filter(x -> !x.getPaid())
                .map(OrderEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private final Logger logger = Logger.getLogger(this.getClass().toString());

    private OrderEntity orderToEntity(OrderRequest order) {
        OrderDetailsEntity details = getDetails(order.toppings(), order.size(), order.dough());
        orderDetailsRepository.save(details);
        logger.info("details: " + details);
        BigDecimal price = getPrice(details);
        logger.info("price: " + price);
        return OrderEntity.builder()
                .data(details)
                .state(OrderState.STORED)
                .paid(false)
                .price(price)
                .time(LocalDateTime.now())
                .amount(1)
                .build();
    }

    private BigDecimal getPrice(OrderDetailsEntity details) {
        BigDecimal sum = details.getSize().getPrice().add(details.getDough().getPrice());
        return sum.add(sumList(details.getToppings()));
    }

    private BigDecimal sumList(List<ToppingCounter> toppings) {
        return toppings.stream()
                .map(tc -> BigDecimal.valueOf(tc.getAmount()).multiply(tc.getTopping().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDetailsEntity getDetails(String[] toppings, String size, String dough) {
        Map<String, ToppingCounter> map = new HashMap<>();
        for (var t : toppings) {
            ToppingCounter counter = map.get(t);
            if (counter == null && !t.isBlank() && !t.isEmpty()) {
                map.put(t, newCounter(t));
            } else if (counter != null) {
                counter.increment();
            }
        }
        return OrderDetailsEntity.builder()
                .dough(findTopping(dough))
                .size(findTopping(size))
                .toppings(map.values().stream().toList())
                .build();
    }

    private ToppingEntity findTopping(String topping) {
        return repository.findByName(topping).orElseThrow(() -> new NotFoundException(String.format("findTopping. Topping %s not found", topping)));
    }

    private ToppingCounter newCounter(String t) {
        ToppingEntity topping = repository.findByName(t).orElseThrow(() -> new NotFoundException(String.format("newCounter. Topping %s not found", t)));
        return ToppingCounter.builder()
                .topping(topping)
                .amount(1)
                .build();
    }

    public Integer getMinimalPrice() {
        return 166;
    }
}
