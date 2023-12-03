package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

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

/**
 * This class contains the business logic for the order.
 * <p>
 *
 * @author Serhii
 * @version 1.0
 * @see OrderEntity
 * @since 1.0
 */

@Service
@AllArgsConstructor
public class OrderService {

    /**
     * The ToppingRepository used to access topping data.
     */
    private final ToppingRepository repository;
    /**
     * The UserRepository used to access user data.
     */
    private final UserRepository userRepository;
    /**
     * The OrderEntityRepository used to access order data.
     */
    private final OrderEntityRepository orderEntityRepository;
    /**
     * The OrderDetailsRepository used to access order details data.
     */
    private final OrderDetailsRepository orderDetailsRepository;

    /**
     * Adds an order to the cart for the user with the given ID.
     * <p>
     * The order details are converted to an OrderEntity and saved.
     * The order is added to the user's order list and the user is saved.
     * The cart total is calculated by summing the prices of all unpaid orders for the user.
     * <p>
     * The order details saved in a separate OrderDetailsEntity associated with the OrderEntity.
     * The price calculated based on the size, dough, and toppings prices.
     *
     * @param order The order details to add to the cart.
     * @param id    The ID of the user to add the order to.
     * @return An OrderResponse with the new order ID and updated cart total.
     * @throws NotFoundException if the user is not found.
     */
    public OrderResponse addOrderToCart(OrderRequest order, Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("not found user: " + id));
        OrderEntity o = orderToEntity(order);
        orderEntityRepository.save(o);
        user.addOrder(o);
        userRepository.save(user);
        return new OrderResponse(o.getId(), calculateCartSum(user.getOrders()));
    }


    /**
     * Calculates the total sum of all unpaid orders in the user's cart.
     *
     * @param orders The list of orders to calculate the cart sum for.
     * @return The total sum of all unpaid orders for the given user.
     * <p>
     * Iterates through the user's order list and sums the prices
     * of all orders with flag paid set to false.
     * <p>
     * The cart sum is used to display the total amount due for the user's cart.
     */
    private BigDecimal calculateCartSum(List<OrderEntity> orders) {
        return orders.stream().filter(x -> !x.getPaid())
                .map(OrderEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Converts an OrderRequest object to an OrderEntity.
     *
     * @param order The Order object to convert.
     * @return The converted OrderEntity.
     */
    private OrderEntity orderToEntity(OrderRequest order) {
        OrderDetailsEntity details = buildOrderDetailsEntity(order.toppings(), order.size(), order.dough());
        orderDetailsRepository.save(details);
        BigDecimal price = getPrice(details);
        return OrderEntity.builder()
                .data(details)
                .state(OrderState.STORED)
                .paid(false)
                .price(price)
                .time(LocalDateTime.now())
                .amount(1)
                .build();
    }

    /**
     * Calculates the price for the given order details.
     *
     * @param details The order details entity to calculate price for.
     * @return The calculated price as a BigDecimal.
     * <p>
     * Gets the base prices for the size and dough from the OrderDetailsEntity.
     * Sums the prices of all the order toppings.
     * Returns the total price by adding the size, dough, and toppings prices.
     */

    private BigDecimal getPrice(OrderDetailsEntity details) {
        BigDecimal sum = details.getSize().getPrice().add(details.getDough().getPrice());
        return sum.add(sumList(details.getToppings()));
    }

    /**
     * Sums the prices in the provided list of toppings.
     *
     * @param toppings The list of toppings to sum.
     * @return The total sum.
     */
    private BigDecimal sumList(List<ToppingCounter> toppings) {
        return toppings.stream()
                .map(tc -> BigDecimal.valueOf(tc.getAmount()).multiply(tc.getTopping().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Builds an OrderDetailsEntity from the given toppings, size, and dough.
     *
     * @param toppings Array of topping names for the order
     * @param size     Name of the pizza size for the order
     * @param dough    Name of the dough for the order
     * @return The built OrderDetailsEntity
     */
    private OrderDetailsEntity buildOrderDetailsEntity(String[] toppings, String size, String dough) {
        Map<String, ToppingCounter> map = new HashMap<>();
        for (var t : toppings) {
            if (t.isBlank()) {
                continue;
            }
            ToppingCounter counter = map.get(t);
            if (counter == null) {
                map.put(t, newCounter(t));
                continue;
            }
            counter.increment();
        }
        return OrderDetailsEntity.builder()
                .dough(findTopping(dough))
                .size(findTopping(size))
                .toppings(map.values().stream().toList())
                .build();
    }

    /**
     * Finds a topping by name.
     *
     * @param topping The name of the topping.
     * @return The ToppingEntity
     * @throws NotFoundException If the topping is not found.
     */
    private ToppingEntity findTopping(String topping) {
        return repository.findByName(topping).orElseThrow(() -> new NotFoundException(String.format("findTopping. Topping %s not found", topping)));
    }

    /**
     * Creates a new ToppingCounter for the given topping name.
     *
     * @param t The name of the topping.
     * @return A new ToppingCounter with amount 1.
     * @throws NotFoundException If the topping is not found.
     *                           <p>
     *                           Looks up the ToppingEntity by name from the repository.
     *                           Throws NotFoundException if no topping found for the name.
     *                           Returns a ToppingCounter with the found ToppingEntity and amount 1.
     */
    private ToppingCounter newCounter(String t) {
        ToppingEntity topping = repository.findByName(t).orElseThrow(() -> new NotFoundException(String.format("newCounter. Topping %s not found", t)));
        return ToppingCounter.builder()
                .topping(topping)
                .amount(1)
                .build();
    }

    /**
     * Gets the minimal possible price for a pizza order.
     *
     * @return The minimal price based on configured base prices.
     */
    public Integer getMinimalPrice() {
        return 166;
    }
}
