package com.pizzacalculator.pizzacalculatorserverside.bussiness.controller;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.*;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.service.CartService;
import com.pizzacalculator.pizzacalculatorserverside.security.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/cart")
@AllArgsConstructor
public class CartController {
    private final CartService service;
    final Logger logger = Logger.getLogger(this.getClass().toString());

    @GetMapping
    public ResponseEntity<CartResponse> getCartByClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("getCartByClient clientId: " + clientId);
        CartResponse response = service.getCart(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/calculate-with-delivery")
    public ResponseEntity<TotalPrice> calculateDelivery(@RequestBody Address address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        TotalPrice response = service.calculateDelivery(clientId, address);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/change-amount")
    public ResponseEntity<CartResponse> changeAmount(@RequestBody ChangeAmountRequest body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("/change-amount: " + body);
        var response = service.changeAmount(body, clientId);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-order")
    public ResponseEntity<CartResponse> deleteOrder(@RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("/delete-order orderId: " + orderId);
        CartResponse response = service.deleteOrder(clientId, orderId);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/checkout/delivery")
    public ResponseEntity<CartResponse> checkoutWithDelivery(@RequestBody Address body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("checkoutWithDelivery" + body.toString());
        CartResponse response = service.checkoutWithDelivery(clientId, true);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/checkout/carryout")
    public ResponseEntity<CartResponse> checkoutCarryOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("checkoutCarryOut: " + clientId);
        CartResponse response = service.checkoutCarryOut(clientId);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-status")
    public ResponseEntity<TimeToWaitResponse> checkStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();
        var clientId = principal.getId();
        logger.info("checkStatus: " + clientId);
        TimeToWaitResponse response = service.checkStatus(clientId);
        logger.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
