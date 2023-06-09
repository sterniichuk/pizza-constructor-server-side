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

    @Column(nullable = false, columnDefinition = "numeric(10,2) default 0.00")
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer amount;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean paid;

    @Column(nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderState state;

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
