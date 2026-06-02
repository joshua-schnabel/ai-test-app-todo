package de.joshuaschnabel.todo.application.port.out;

public interface PasswordHashingPort {

    String hash(String plainPassword);

    boolean matches(String plain, String hash);
}
