package de.joshuaschnabel.todo.infrastruktur.security.adapter;

import de.joshuaschnabel.todo.application.port.out.PasswordHashingPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHashingAdapter implements PasswordHashingPort {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plain, String hash) {
        return passwordEncoder.matches(plain, hash);
    }
}
