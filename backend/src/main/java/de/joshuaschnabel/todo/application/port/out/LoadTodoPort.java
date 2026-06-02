package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.Todo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadTodoPort {

    List<Todo> loadByListId(UUID listId);

    Optional<Todo> loadById(UUID id);
}
