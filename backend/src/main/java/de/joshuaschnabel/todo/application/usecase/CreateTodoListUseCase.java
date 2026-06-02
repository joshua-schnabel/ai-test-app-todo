package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.CreateTodoListCommand;
import de.joshuaschnabel.todo.domain.model.TodoList;

public interface CreateTodoListUseCase {

    TodoList createList(CreateTodoListCommand cmd);
}
