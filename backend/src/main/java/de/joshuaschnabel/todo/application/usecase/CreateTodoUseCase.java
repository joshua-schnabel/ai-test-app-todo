package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.CreateTodoCommand;
import de.joshuaschnabel.todo.domain.model.Todo;

public interface CreateTodoUseCase {

    Todo createTodo(CreateTodoCommand cmd);
}
