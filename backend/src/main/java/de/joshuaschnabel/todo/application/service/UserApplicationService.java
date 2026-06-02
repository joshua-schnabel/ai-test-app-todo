package de.joshuaschnabel.todo.application.service;

import de.joshuaschnabel.todo.application.command.CreateTodoListCommand;
import de.joshuaschnabel.todo.application.command.LoginUserCommand;
import de.joshuaschnabel.todo.application.command.RegisterUserCommand;
import de.joshuaschnabel.todo.application.exception.EmailAlreadyExistsException;
import de.joshuaschnabel.todo.application.exception.InvalidCredentialsException;
import de.joshuaschnabel.todo.application.exception.NotFoundException;
import de.joshuaschnabel.todo.application.port.out.LoadUserPort;
import de.joshuaschnabel.todo.application.port.out.PasswordHashingPort;
import de.joshuaschnabel.todo.application.port.out.SaveUserPort;
import de.joshuaschnabel.todo.application.port.out.TokenServicePort;
import de.joshuaschnabel.todo.application.usecase.CreateTodoListUseCase;
import de.joshuaschnabel.todo.application.usecase.GetCurrentUserUseCase;
import de.joshuaschnabel.todo.application.usecase.LoginUserUseCase;
import de.joshuaschnabel.todo.application.usecase.RegisterUserUseCase;
import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.domain.valueobject.EmailAddress;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public class UserApplicationService implements RegisterUserUseCase, LoginUserUseCase, GetCurrentUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordHashingPort passwordHashingPort;
    private final TokenServicePort tokenServicePort;
    private final CreateTodoListUseCase createTodoListUseCase;

    public UserApplicationService(LoadUserPort loadUserPort, SaveUserPort saveUserPort,
                                  PasswordHashingPort passwordHashingPort, TokenServicePort tokenServicePort,
                                  CreateTodoListUseCase createTodoListUseCase) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.passwordHashingPort = passwordHashingPort;
        this.tokenServicePort = tokenServicePort;
        this.createTodoListUseCase = createTodoListUseCase;
    }

    @Override
    @Transactional
    public User register(RegisterUserCommand cmd) {
        EmailAddress emailAddress = new EmailAddress(cmd.email());
        loadUserPort.loadByEmail(emailAddress.value()).ifPresent(existing -> {
            throw new EmailAlreadyExistsException("E-Mail ist bereits registriert");
        });

        Instant now = Instant.now();
        User savedUser = saveUserPort.save(new User(
                UUID.randomUUID(),
                emailAddress,
                passwordHashingPort.hash(cmd.password()),
                now,
                now
        ));

        createTodoListUseCase.createList(new CreateTodoListCommand(savedUser.getId(), "Meine Aufgaben"));
        return savedUser;
    }

    @Override
    public String login(LoginUserCommand cmd) {
        EmailAddress emailAddress = new EmailAddress(cmd.email());
        User user = loadUserPort.loadByEmail(emailAddress.value())
                .orElseThrow(() -> new InvalidCredentialsException("Ungültige Anmeldedaten"));

        if (!passwordHashingPort.matches(cmd.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Ungültige Anmeldedaten");
        }
        return tokenServicePort.generateToken(user.getId());
    }

    @Override
    public User getCurrentUser(UUID userId) {
        return loadUserPort.loadById(userId)
                .orElseThrow(() -> new NotFoundException("Benutzer wurde nicht gefunden"));
    }
}
