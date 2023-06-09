package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Topping(String name, float price, int max) {
    public BigDecimal getPrice() {
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}
