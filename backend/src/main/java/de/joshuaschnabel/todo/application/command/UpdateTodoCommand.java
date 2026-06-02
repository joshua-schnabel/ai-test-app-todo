package de.joshuaschnabel.todo.application.command;

import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTodoCommand(UUID ownerId, UUID listId, UUID todoId, String title, String description,
                                TodoPriority priority, LocalDate dueDate) {
}
