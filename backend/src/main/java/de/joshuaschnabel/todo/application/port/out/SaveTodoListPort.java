package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.TodoList;

public interface SaveTodoListPort {

    TodoList save(TodoList list);
}
