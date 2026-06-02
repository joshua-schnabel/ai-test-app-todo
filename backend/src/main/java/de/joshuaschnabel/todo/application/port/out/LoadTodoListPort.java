package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.TodoList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadTodoListPort {

    List<TodoList> loadByOwner(UUID ownerId);

    Optional<TodoList> loadById(UUID id);
}
