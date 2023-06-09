package com.pizzacalculator.pizzacalculatorserverside.security.service;

import com.pizzacalculator.pizzacalculatorserverside.security.model.AuthResponse;
import com.pizzacalculator.pizzacalculatorserverside.security.model.LoginData;
import com.pizzacalculator.pizzacalculatorserverside.security.model.Role;
import com.pizzacalculator.pizzacalculatorserverside.security.model.UserTempData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    public static final Map<String, UserTempData> phoneToUser = new HashMap<>();
    public static final Map<String, UserTempData> tokenToUser = new HashMap<>();
    private final AuthenticationServiceImpl authService;

    public AuthResponse add(LoginData user) {
        return authService.register(user, Role.USER);
    }

    public AuthResponse get(LoginData login) {
        return authService.authenticate(login);
    }
}
