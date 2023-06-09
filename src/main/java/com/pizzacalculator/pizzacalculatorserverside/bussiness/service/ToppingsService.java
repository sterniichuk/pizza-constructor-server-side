package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.Topping;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.ToppingCategory;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingCategoryEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.ToppingCategoryRepository;
import com.pizzacalculator.pizzacalculatorserverside.security.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


@Service
@AllArgsConstructor
public class ToppingsService {
    private final FileServiceImpl fileService;
    private final ToppingCategoryRepository repository;

    public Resource downloadPhotoByToppingName(String category, String topping) {
        String path = "food" + $ + category + $ + topping + ".jpeg";
        Resource read;
        try {
            read = fileService.read(path);
        } catch (Exception e) {
            read = fileService.read("not-found.webp");
            e.printStackTrace();
        }
        return read;
    }

    private final String $ = File.separator;

    public List<Topping> getSize() {
        return getToppingsByCategory("size");
    }

    @NotNull
    private List<Topping> getToppingsByCategory(String categoryName) {
        ToppingCategoryEntity toppings = repository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new NotFoundException(String.format("getToppingsByCategory. Category %s not found", categoryName)));
        return getList(toppings);
    }

    @NotNull
    private static List<Topping> getList(ToppingCategoryEntity toppings) {
        return toppings.getToppings().stream()
                .map(t -> new Topping(t.getName(),
                        t.getPrice().floatValue(),
                        Math.min(t.getMax(), t.getAmount())))
                .toList();
    }

    public List<Topping> getDough() {
        return getToppingsByCategory("dough");
    }


    public List<ToppingCategory> getUserSelectToppings() {
        return repository.findAll()
                .stream()
                .filter(c -> !c.getName().equalsIgnoreCase("dough") &&
                        !c.getName().equalsIgnoreCase("size"))
                .map(c -> new ToppingCategory(c.getName(), getList(c)))
                .toList();
    }
}
