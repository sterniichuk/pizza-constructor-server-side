package com.pizzacalculator.pizzacalculatorserverside.security.service;

import com.pizzacalculator.pizzacalculatorserverside.security.model.*;
import com.pizzacalculator.pizzacalculatorserverside.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.pizzacalculator.pizzacalculatorserverside.bussiness.service.OrderService.map;
import static com.pizzacalculator.pizzacalculatorserverside.security.service.UserService.phoneToUser;
import static com.pizzacalculator.pizzacalculatorserverside.security.service.UserService.tokenToUser;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = Logger.getLogger(this.getClass().toString());


    public AuthResponse register(LoginData request, Role role) {
        final String phone = request.phoneNumber();
        final String password = request.password();
        if (repository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone: " + phone + " is already in use");
        }
        var user = User.builder()
                .phone(phone)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .build();
        repository.save(user);
        logger.info("saved: " + user);
        //
        var id = user.getId();
        UserTempData value = new UserTempData(id, request);
        phoneToUser.put(user.getPhone(), value);
        map.put(id, new ArrayList<>());
        //
        String jwtToken = jwtService.generateToken(user);
        logger.info("jwtToken: " + jwtToken);

        //
        tokenToUser.put(jwtToken, value);
        //
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(LoginData request) {
        String phone = request.phoneNumber();
        String password = request.password();
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        phone,
                        password
                )
        );
        logger.info("authenticationManager.authenticate: " + authenticate);
        var user = repository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("User with phone: " + phone + " not found"));
        var jwtToken = jwtService.generateToken(user);
        logger.info("jwtToken: " + jwtToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
