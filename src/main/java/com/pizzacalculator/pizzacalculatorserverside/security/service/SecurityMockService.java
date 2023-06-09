package com.pizzacalculator.pizzacalculatorserverside.security.service;

import com.pizzacalculator.pizzacalculatorserverside.security.model.UserTempData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static com.pizzacalculator.pizzacalculatorserverside.security.service.UserService.tokenToUser;

@Service
public class SecurityMockService {
    final Logger logger = Logger.getLogger(this.getClass().toString());

    public Long getId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        logger.info("request.getHeader: " + token);
        UserTempData userTempData = tokenToUser.get(token);
        if(userTempData == null){
            return null;
        }
        return userTempData.id();
    }
}
