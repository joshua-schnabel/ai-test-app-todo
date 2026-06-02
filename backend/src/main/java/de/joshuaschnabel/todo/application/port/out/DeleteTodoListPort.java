package de.joshuaschnabel.todo.application.port.out;

import java.util.UUID;

public interface DeleteTodoListPort {

    void deleteById(UUID id);
}
