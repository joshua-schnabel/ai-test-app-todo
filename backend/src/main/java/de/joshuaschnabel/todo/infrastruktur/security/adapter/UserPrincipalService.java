package de.joshuaschnabel.todo.infrastruktur.security.adapter;

import de.joshuaschnabel.todo.application.port.out.LoadUserPort;
import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.infrastruktur.security.model.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPrincipalService implements UserDetailsService {

    private final LoadUserPort loadUserPort;

    public UserPrincipalService(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    public UserPrincipal loadByUserId(UUID userId) {
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(user.getId(), user.getEmail().value());
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadByUserId(UUID.fromString(username));
        } catch (IllegalArgumentException ex) {
            throw new UsernameNotFoundException("User not found", ex);
        }
    }
}
