package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record RenameTodoListCommand(UUID ownerId, UUID listId, String newName) {
}
