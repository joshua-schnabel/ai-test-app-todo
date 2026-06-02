package de.joshuaschnabel.todo.infrastruktur.presentation.rest.response;

import java.time.Instant;
import java.util.UUID;

public record TodoListResponse(UUID id, String name, Instant createdAt, Instant updatedAt) {
}
