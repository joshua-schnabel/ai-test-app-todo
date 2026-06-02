package de.joshuaschnabel.todo.infrastruktur.presentation.rest.request;

import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTodoRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 1000) String description,
        TodoPriority priority,
        LocalDate dueDate
) {
    public UpdateTodoRequest {
        priority = priority == null ? TodoPriority.MEDIUM : priority;
    }
}
