package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderInCart;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "order_in_cart")
@Table(name = "order_in_cart")
public class OrderEntity {
    @Id
    @SequenceGenerator(
            name = "order_in_cart_sequence",
            sequenceName = "order_in_cart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_in_cart_sequence"
    )
    @Column(name = "id")
    private Long id;

    /**
     * The price field, stores the total price of the order.
     * Default 0.00 if not specified.
     */
    @Column(nullable = false, columnDefinition = "numeric(10,2) default 0.00")
    private BigDecimal price;
    /**
     * The amount field, stores the number of this pizza ordered.
     * Default 0 if not specified.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer amount;
    /**
     * The paid field, boolean indicating if order is paid or not.
     * Default false if not specified.
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean paid;
    /**
     * The time field, stores the date/time the order was created.
     *
     */
    @Column(nullable = false)
    private LocalDateTime time;
    /**
     * The state field, enum indicating order state (in progress, complete, etc.)
     * Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderState state;
    /**
     * The data field, one-to-one mapping to OrderDetailsEntity.
     * Contains full order details. Fetched lazily.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private OrderDetailsEntity data;

    public void changeAmount(int diff) {
        this.amount += diff;
    }

    public OrderInCart getDTO() {
        return OrderInCart.builder()
                .orderId(id)
                .price(price)
                .amount(amount)
                .isPaid(paid)
                .state(state)
                .data(data.getDTO())
                .build();
    }
}
