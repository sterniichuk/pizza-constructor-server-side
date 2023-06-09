package com.pizzacalculator.pizzacalculatorserverside.security.model;

import lombok.Builder;

@Builder
public record AuthResponse(String token) {
}
