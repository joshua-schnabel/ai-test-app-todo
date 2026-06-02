package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.RenameTodoListCommand;
import de.joshuaschnabel.todo.domain.model.TodoList;

public interface RenameTodoListUseCase {

    TodoList renameList(RenameTodoListCommand cmd);
}
