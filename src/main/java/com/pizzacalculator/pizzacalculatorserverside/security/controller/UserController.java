package com.pizzacalculator.pizzacalculatorserverside.security.controller;

import com.pizzacalculator.pizzacalculatorserverside.security.service.UserService;
import com.pizzacalculator.pizzacalculatorserverside.security.model.AuthResponse;
import com.pizzacalculator.pizzacalculatorserverside.security.model.LoginData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Logger;
@RestController
@RequestMapping("api/v1/user")
@CrossOrigin(origins = "localhost:3000/*")
@AllArgsConstructor
public class UserController {
    public final UserService service;
    private final Logger logger = Logger.getLogger(this.getClass().toString());
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody LoginData user) {
        logger.info("register: " + user);
        AuthResponse register = service.add(user);
        logger.info("register token: " + register);
        return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@Valid @RequestBody LoginData user) {
        logger.info("auth: " + user);
        AuthResponse register = service.get(user);
        logger.info("auth token: " + register);
        return new ResponseEntity<>(register, HttpStatus.OK);
    }
}
