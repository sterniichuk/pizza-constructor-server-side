package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import java.util.List;

public record ToppingCategory(String name, List<Topping> toppings) {
}
