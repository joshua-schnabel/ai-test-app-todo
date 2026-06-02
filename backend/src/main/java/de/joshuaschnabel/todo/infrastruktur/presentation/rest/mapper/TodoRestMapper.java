package de.joshuaschnabel.todo.infrastruktur.presentation.rest.mapper;

import de.joshuaschnabel.todo.domain.model.Todo;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.TodoResponse;
import org.springframework.stereotype.Component;

@Component
public class TodoRestMapper {

    public TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getListId(),
                todo.getTitle().value(),
                todo.getDescription(),
                todo.getStatus().name(),
                todo.getPriority().name(),
                todo.getDueDate(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
