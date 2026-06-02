package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record ReopenTodoCommand(UUID ownerId, UUID listId, UUID todoId) {
}
