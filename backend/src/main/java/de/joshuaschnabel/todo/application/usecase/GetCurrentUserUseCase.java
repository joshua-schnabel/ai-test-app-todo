package de.joshuaschnabel.todo.application.usecase;

import de.joshuaschnabel.todo.domain.model.User;

import java.util.UUID;

public interface GetCurrentUserUseCase {

    User getCurrentUser(UUID userId);
}
