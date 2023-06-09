package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record OrderDetails(String size, String dough, List<String> toppings) {
}
