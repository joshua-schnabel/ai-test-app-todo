package de.joshuaschnabel.todo.infrastruktur.presentation.rest.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TodoResponse(UUID id, UUID listId, String title, String description, String status, String priority,
                           LocalDate dueDate, Instant createdAt, Instant updatedAt) {
}
