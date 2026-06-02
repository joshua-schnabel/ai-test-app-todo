package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.DeleteTodoCommand;

public interface DeleteTodoUseCase {

    void deleteTodo(DeleteTodoCommand cmd);
}
