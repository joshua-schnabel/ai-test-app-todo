package de.joshuaschnabel.todo.application.query;

import java.util.UUID;

public record GetTodosQuery(UUID ownerId, UUID listId, String statusFilter, String sortBy, String sortDir) {
}
