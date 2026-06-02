package de.joshuaschnabel.todo.infrastruktur.presentation.rest.controller;

import de.joshuaschnabel.todo.application.command.LoginUserCommand;
import de.joshuaschnabel.todo.application.command.RegisterUserCommand;
import de.joshuaschnabel.todo.application.usecase.GetCurrentUserUseCase;
import de.joshuaschnabel.todo.application.usecase.LoginUserUseCase;
import de.joshuaschnabel.todo.application.usecase.RegisterUserUseCase;
import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.LoginRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.RegisterUserRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.AuthResponse;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.UserResponse;
import de.joshuaschnabel.todo.infrastruktur.security.adapter.CurrentUserProviderAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final CurrentUserProviderAdapter currentUserProviderAdapter;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          CurrentUserProviderAdapter currentUserProviderAdapter) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.currentUserProviderAdapter = currentUserProviderAdapter;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = registerUserUseCase.register(new RegisterUserCommand(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(user.getId(), user.getEmail().value()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUserUseCase.login(new LoginUserCommand(request.email(), request.password()));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User user = getCurrentUserUseCase.getCurrentUser(currentUserProviderAdapter.getCurrentUserId());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail().value()));
    }
}
