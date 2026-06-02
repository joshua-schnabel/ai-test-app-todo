package de.joshuaschnabel.todo.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface TokenServicePort {

    String generateToken(UUID userId);

    Optional<UUID> extractUserId(String token);
}
