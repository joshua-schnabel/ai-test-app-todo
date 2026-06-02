package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.DeleteTodoListCommand;

public interface DeleteTodoListUseCase {

    void deleteList(DeleteTodoListCommand cmd);
}
