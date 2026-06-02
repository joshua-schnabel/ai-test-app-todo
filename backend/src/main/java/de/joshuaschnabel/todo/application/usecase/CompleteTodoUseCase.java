package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.CompleteTodoCommand;
import de.joshuaschnabel.todo.domain.model.Todo;

public interface CompleteTodoUseCase {

    Todo completeTodo(CompleteTodoCommand cmd);
}
