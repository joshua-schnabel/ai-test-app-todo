package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.UpdateTodoCommand;
import de.joshuaschnabel.todo.domain.model.Todo;

public interface UpdateTodoUseCase {

    Todo updateTodo(UpdateTodoCommand cmd);
}
