package de.joshuaschnabel.todo.application.port.out;

import de.joshuaschnabel.todo.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<User> loadByEmail(String email);

    Optional<User> loadById(UUID id);
}
