package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public final class OrderInCart {
    private final long orderId;
    private BigDecimal price;
    private int amount;
    private boolean isPaid;
    private OrderState state;
    private final OrderDetails data;
}


