package com.pizzacalculator.pizzacalculatorserverside.bussiness.controller;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.Topping;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.service.DatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/database/fill")
@AllArgsConstructor
public class DatabaseController {
    private final DatabaseService service;
    final Logger logger = Logger.getLogger(this.getClass().toString());

    @PostMapping("/sizes")
    public ResponseEntity<List<Topping>> addSizes() {
        logger.info("addSizes()");
        var response = service.addSizes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/dough")
    public ResponseEntity<List<Topping>> addDough() {
        logger.info("addDough()");
        var response = service.addDough();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user-toppings")
    public ResponseEntity<List<?>> addUserSelectToppings() {
        logger.info("addUserSelectToppings()");
        var response = service.addUserSelectToppings();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/all")
    public ResponseEntity<List<?>> addAll() {
        logger.info("addAll");
        var response = service.addAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
