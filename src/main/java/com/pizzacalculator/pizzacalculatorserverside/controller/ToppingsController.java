package com.pizzacalculator.pizzacalculatorserverside.controller;

import com.pizzacalculator.pizzacalculatorserverside.model.Topping;
import com.pizzacalculator.pizzacalculatorserverside.model.ToppingCategory;
import com.pizzacalculator.pizzacalculatorserverside.service.ToppingsService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("api/v1/topping")
@CrossOrigin(origins = "localhost:3000/*")
@AllArgsConstructor
public class ToppingsController {

    ToppingsService service;

    @GetMapping("/size")
    public ResponseEntity<List<Topping>> get() {
        var response = List.of(
                new Topping("Standard size", 100, true),
                new Topping("Large", 200, true),
                new Topping("ExtraLarge", 300, true),
                new Topping("XXLarge", 500, false)
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dough")
    public ResponseEntity<List<Topping>> getDough() {
        List<String> list = List.of("Thick crust", "Thin", "Philadelphia", "Hot-Dog");
        return new ResponseEntity<>(convertStringToTopping(list, 100), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ToppingCategory>> getCategories() {
//        List<String> list = List.of("Vegetables", "Sauces", "Meats", "Cheeses");
        List<String> cheeses = List.of("Cheddar", "Mozarella", "Parmesan", "Feta", "Bergader Blue");
        List<String> vegetables = List.of("Jalapeno",
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
        List<String> sauces = List.of("Garlic sauce",
                "BBQ sauce",
                "BBQ sauce (on top)",
                "Domino's sauce",
                "Al'fredo sauce");
        List<String> meats = List.of("Ham",
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
        List<ToppingCategory> response = List.of(
                new ToppingCategory("meats", convertStringToTopping(meats, 333)),
                new ToppingCategory("sauces", convertStringToTopping(sauces, 23)),
                new ToppingCategory("vegetables", convertStringToTopping(vegetables, 13)),
                new ToppingCategory("cheeses", convertStringToTopping(cheeses, 198))
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    private List<Topping> convertStringToTopping(List<String> list, int base){
        List<Topping> response = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            response.add(new Topping(list.get(i), i * base, i % 2 != 0));
        }
        return response;
    }
    @GetMapping("/download/{category}/{topping}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String category, @PathVariable String topping) {
        Resource file = service.downloadPhotoByToppingName(category, topping);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
