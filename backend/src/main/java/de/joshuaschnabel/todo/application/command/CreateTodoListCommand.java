package de.joshuaschnabel.todo.application.command;

import java.util.UUID;

public record CreateTodoListCommand(UUID ownerId, String name) {
}
