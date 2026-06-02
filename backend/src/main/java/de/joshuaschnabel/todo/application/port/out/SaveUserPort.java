package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.User;

public interface SaveUserPort {

    User save(User user);
}
