package com.pizzacalculator.pizzacalculatorserverside.security.model;

import javax.validation.constraints.NotBlank;

public record LoginData(
        @NotBlank String phoneNumber,
        @NotBlank String password
) {
}
