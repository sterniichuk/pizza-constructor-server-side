package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.Topping;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.ToppingCategory;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingCategoryEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.ToppingCategoryRepository;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.ToppingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class DatabaseService {

    private final ToppingCategoryRepository categoryRepository;
    public static final List<Topping> sizesOfPizza = List.of(
            new Topping("Standard size", 100, 1),
            new Topping("Large", 200, 1),
            new Topping("ExtraLarge", 300, 1),
            new Topping("XXLarge", 500, 1)
    );
    public static final List<String> dough = List.of("Thick crust", "Thin", "Philadelphia", "Hot-Dog");
    public static final List<String> cheeses = List.of("Cheddar", "Mozarella", "Parmesan", "Feta", "Bergader Blue");
    public static final List<String> vegetables = List.of("Jalapeno",
            "Mushrooms",
            "Cherry tomatoes",
            "Tomatoes",
            "Olives",
            "Mustard",
            "Pineapple",
            "Pickled cucumbers",
            "Spinach",
            "Corn",
            "Onion",
            "Sweet pepper");
    public static final List<String> sauces = List.of("Garlic sauce",
            "BBQ sauce",
            "BBQ sauce (on top)",
            "Domino's sauce",
            "Al'fredo sauce");
    public static final List<String> meats = List.of("Ham",
            "Chorizo",
            "Tuna",
            "White sausages",
            "Turkey",
            "Peperoni",
            "Bavarian sausages",
            "Veal",
            "Bacon",
            "Chicken",
            "Meatballs");
    public static final List<ToppingCategory> all = List.of(
            new ToppingCategory("meats", convertStringToTopping(meats, 13, (i) -> i % 7)),
            new ToppingCategory("sauces", convertStringToTopping(sauces, 4, (i) -> 5)),
            new ToppingCategory("vegetables", convertStringToTopping(vegetables, 4, (i) -> i % 8)),
            new ToppingCategory("cheeses", convertStringToTopping(cheeses, 33, (i) -> i % 8))
    );

    private static List<Topping> convertStringToTopping(List<String> list, int base, Function<Integer, Integer> max) {
        List<Topping> response = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            response.add(new Topping(list.get(i), (i + 1) * base, max.apply(i)));
        }
        return response;
    }

    public List<Topping> addSizes() {
        addToppings("Size", sizesOfPizza);
        return sizesOfPizza;
    }

    public List<Topping> addDough() {
        List<Topping> toppings = convertStringToTopping(dough, 33, (i) -> i);
        addToppings("Dough", toppings);
        return toppings;
    }

    public List<ToppingCategory> addUserSelectToppings() {
        all.forEach(category -> addToppings(category.name(), category.toppings()));
        return all;
    }

    public List<Object> addAll() {
        addSizes();
        addDough();
        addUserSelectToppings();
        return List.of(sizesOfPizza, dough, all);
    }
    final Logger logger = Logger.getLogger(this.getClass().toString());

    private void addToppings(String name, List<Topping> toppings) {
        var category = categoryRepository.findByNameIgnoreCase(name)
                .orElse(createNewCategory(name));
        var toppingEntities = category.getToppings();
        if (toppingEntities.isEmpty()) {
            toppings.forEach(s -> {
                var topping = ToppingEntity.builder()
                        .amount(1000)
                        .max(s.max())
                        .price(s.getPrice())
                        .name(s.name()).build();
                category.addTopping(topping);
            });
        } else {
            toppingEntities.forEach(t -> t.setAmount(1000));
        }
        categoryRepository.save(category);
        logger.info(category.getToppings().toString());
    }

    private ToppingCategoryEntity createNewCategory(String name) {
        var newCategory = new ToppingCategoryEntity(name);
        categoryRepository.save(newCategory);
        return newCategory;
    }
}
