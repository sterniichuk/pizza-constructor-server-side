package com.pizzacalculator.pizzacalculatorserverside.model;

import java.util.List;

public record ToppingCategory(String name, List<Topping> toppings) {
}
