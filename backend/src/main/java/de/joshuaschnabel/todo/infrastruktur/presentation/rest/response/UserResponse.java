package de.joshuaschnabel.todo.infrastruktur.presentation.rest.response;

import java.util.UUID;

public record UserResponse(UUID id, String email) {
}
