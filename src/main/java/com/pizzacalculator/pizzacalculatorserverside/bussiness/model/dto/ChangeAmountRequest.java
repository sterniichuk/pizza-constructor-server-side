package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

public record ChangeAmountRequest(Long orderId, Boolean isPositiveChange) {
}
