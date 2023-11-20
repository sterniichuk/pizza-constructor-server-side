package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.math.BigDecimal;

public record TotalPrice(BigDecimal goodsPrice, BigDecimal totalSum) {
}
