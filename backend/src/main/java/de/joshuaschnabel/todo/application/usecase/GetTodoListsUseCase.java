package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.domain.model.TodoList;

import java.util.List;
import java.util.UUID;

public interface GetTodoListsUseCase {

    List<TodoList> getLists(UUID ownerId);
}
