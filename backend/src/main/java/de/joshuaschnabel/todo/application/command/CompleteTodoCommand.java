package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record CompleteTodoCommand(UUID ownerId, UUID listId, UUID todoId) {
}
