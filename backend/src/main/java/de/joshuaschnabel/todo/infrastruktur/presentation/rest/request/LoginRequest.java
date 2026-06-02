package de.joshuaschnabel.todo.infrastruktur.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
