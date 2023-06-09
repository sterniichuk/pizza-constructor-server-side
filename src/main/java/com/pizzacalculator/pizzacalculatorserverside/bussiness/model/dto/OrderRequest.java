package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;


import java.util.Arrays;

public record OrderRequest(String size, String dough, String[] toppings) {
    @Override
    public String toString() {
        return String.format("OrderRequest(size: %s, dough: %s, toppings: %s)", size, dough, Arrays.stream(toppings).toList());
    }
}
