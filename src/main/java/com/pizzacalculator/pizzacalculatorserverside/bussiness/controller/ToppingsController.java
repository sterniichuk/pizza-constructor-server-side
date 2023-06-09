package com.pizzacalculator.pizzacalculatorserverside.bussiness.controller;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.Topping;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.ToppingCategory;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.service.ToppingsService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/topping")
@AllArgsConstructor
public class ToppingsController {

    private final ToppingsService service;

    @GetMapping("/size")
    public ResponseEntity<List<Topping>> get() {
        List<Topping> response = service.getSize();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dough")
    public ResponseEntity<List<Topping>> getDough() {
        List<Topping> response = service.getDough();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ToppingCategory>> getCategories() {
        List<ToppingCategory> response = service.getUserSelectToppings();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/download/{category}/{topping}")
    public ResponseEntity<Resource> serveFile(@PathVariable String category, @PathVariable String topping) {
        Resource file = service.downloadPhotoByToppingName(category, topping);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
