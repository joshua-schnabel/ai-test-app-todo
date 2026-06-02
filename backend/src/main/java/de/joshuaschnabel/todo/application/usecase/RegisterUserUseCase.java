package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.RegisterUserCommand;
import de.joshuaschnabel.todo.domain.model.User;

public interface RegisterUserUseCase {

    User register(RegisterUserCommand cmd);
}
