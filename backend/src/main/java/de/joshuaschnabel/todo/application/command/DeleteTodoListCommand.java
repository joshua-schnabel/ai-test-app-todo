package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record DeleteTodoListCommand(UUID ownerId, UUID listId) {
}
