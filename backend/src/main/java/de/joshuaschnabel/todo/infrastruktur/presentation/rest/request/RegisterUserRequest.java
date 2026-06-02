package de.joshuaschnabel.todo.infrastruktur.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank String email,
        @NotBlank @Size(min = 8) String password
) {
}
