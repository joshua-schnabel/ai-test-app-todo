package de.joshuaschnabel.todo.application.service;

import de.joshuaschnabel.todo.application.command.CreateTodoListCommand;
import de.joshuaschnabel.todo.application.command.LoginUserCommand;
import de.joshuaschnabel.todo.application.command.RegisterUserCommand;
import de.joshuaschnabel.todo.application.exception.EmailAlreadyExistsException;
import de.joshuaschnabel.todo.application.exception.InvalidCredentialsException;
import de.joshuaschnabel.todo.application.port.out.LoadUserPort;
import de.joshuaschnabel.todo.application.port.out.PasswordHashingPort;
import de.joshuaschnabel.todo.application.port.out.SaveUserPort;
import de.joshuaschnabel.todo.application.port.out.TokenServicePort;
import de.joshuaschnabel.todo.application.usecase.CreateTodoListUseCase;
import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.domain.valueobject.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private SaveUserPort saveUserPort;
    @Mock
    private PasswordHashingPort passwordHashingPort;
    @Mock
    private TokenServicePort tokenServicePort;
    @Mock
    private CreateTodoListUseCase createTodoListUseCase;

    @InjectMocks
    private UserApplicationService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), new EmailAddress("user@example.com"), "hashed-password", Instant.now(), Instant.now());
    }

    @Test
    void registrationCreatesUserAndDefaultList() {
        when(loadUserPort.loadByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordHashingPort.hash("password123")).thenReturn("hashed-password");
        when(saveUserPort.save(any(User.class))).thenReturn(user);

        User result = service.register(new RegisterUserCommand("user@example.com", "password123"));

        assertThat(result).isEqualTo(user);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(saveUserPort).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail().value()).isEqualTo("user@example.com");
        verify(createTodoListUseCase).createList(new CreateTodoListCommand(user.getId(), "Meine Aufgaben"));
    }

    @Test
    void duplicateEmailThrowsEmailAlreadyExistsException() {
        when(loadUserPort.loadByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.register(new RegisterUserCommand("user@example.com", "password123")))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void loginWithCorrectCredentialsReturnsToken() {
        when(loadUserPort.loadByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHashingPort.matches("password123", "hashed-password")).thenReturn(true);
        when(tokenServicePort.generateToken(user.getId())).thenReturn("jwt-token");

        String token = service.login(new LoginUserCommand("user@example.com", "password123"));

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void loginWithWrongPasswordThrowsInvalidCredentialsException() {
        when(loadUserPort.loadByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHashingPort.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginUserCommand("user@example.com", "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
