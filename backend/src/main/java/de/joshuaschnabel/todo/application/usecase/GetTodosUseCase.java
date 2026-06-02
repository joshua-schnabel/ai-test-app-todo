package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.query.GetTodosQuery;
import de.joshuaschnabel.todo.domain.model.Todo;

import java.util.List;

public interface GetTodosUseCase {

    List<Todo> getTodos(GetTodosQuery query);
}
