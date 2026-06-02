package de.joshuaschnabel.todo.infrastruktur.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoListRequest(
        @NotBlank @Size(max = 80) String name
) {
}
