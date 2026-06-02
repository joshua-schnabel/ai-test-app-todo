package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.Todo;

public interface SaveTodoPort {

    Todo save(Todo todo);
}
