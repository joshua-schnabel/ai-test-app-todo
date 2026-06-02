package de.joshuaschnabel.todo.application.port.out;

import java.util.UUID;

public interface DeleteTodoPort {

    void deleteById(UUID id);
}
