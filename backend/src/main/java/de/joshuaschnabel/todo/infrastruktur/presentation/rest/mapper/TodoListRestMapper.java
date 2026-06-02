package de.joshuaschnabel.todo.infrastruktur.presentation.rest.mapper;

import de.joshuaschnabel.todo.domain.model.TodoList;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.TodoListResponse;
import org.springframework.stereotype.Component;

@Component
public class TodoListRestMapper {

    public TodoListResponse toResponse(TodoList todoList) {
        return new TodoListResponse(
                todoList.getId(),
                todoList.getName().value(),
                todoList.getCreatedAt(),
                todoList.getUpdatedAt()
        );
    }
}
