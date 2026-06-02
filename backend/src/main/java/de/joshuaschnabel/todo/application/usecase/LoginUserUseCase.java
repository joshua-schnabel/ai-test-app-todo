package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.application.command.LoginUserCommand;

public interface LoginUserUseCase {

    String login(LoginUserCommand cmd);
}
