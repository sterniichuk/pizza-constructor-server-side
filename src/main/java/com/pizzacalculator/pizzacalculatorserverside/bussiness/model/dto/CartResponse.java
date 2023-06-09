package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(BigDecimal cartSum, List<OrderInCart> orders) {
}
