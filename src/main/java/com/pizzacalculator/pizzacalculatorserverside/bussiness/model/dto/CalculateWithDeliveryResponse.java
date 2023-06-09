package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.math.BigDecimal;

public record CalculateWithDeliveryResponse(BigDecimal goodsPrice, BigDecimal totalSum) {
}
