package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.ReopenTodoCommand;
import de.joshuaschnabel.todo.domain.model.Todo;

public interface ReopenTodoUseCase {

    Todo reopenTodo(ReopenTodoCommand cmd);
}
