package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record DeleteTodoCommand(UUID ownerId, UUID listId, UUID todoId) {
}
