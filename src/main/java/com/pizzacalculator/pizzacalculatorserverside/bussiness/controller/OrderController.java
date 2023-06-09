package com.pizzacalculator.pizzacalculatorserverside.bussiness.controller;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderRequest;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderResponse;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.service.OrderService;
import com.pizzacalculator.pizzacalculatorserverside.security.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/order")
@AllArgsConstructor
public class OrderController {

    public final OrderService service;
    private final Logger logger = Logger.getLogger(this.getClass().toString());

    @PostMapping
    public ResponseEntity<OrderResponse> addToCart(@RequestBody OrderRequest order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        logger.info("addToCart: " + order);
        OrderResponse response = service.addOrderToCart(order, principal.getId());
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/minimal-price")
    public ResponseEntity<Integer> getMinimalPrice() {
        Integer response = service.getMinimalPrice();
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
