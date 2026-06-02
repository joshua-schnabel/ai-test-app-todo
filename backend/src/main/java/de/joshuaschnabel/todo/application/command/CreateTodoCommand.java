package de.joshuaschnabel.todo.application.command;

import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTodoCommand(UUID ownerId, UUID listId, String title, String description,
                                TodoPriority priority, LocalDate dueDate) {
}
