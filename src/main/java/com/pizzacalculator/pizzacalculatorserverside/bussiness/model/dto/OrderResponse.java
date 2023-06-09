package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.math.BigDecimal;

public record OrderResponse(long orderId, BigDecimal cartSum) {
}
