package de.joshuaschnabel.todo.domain.valueobject;

import de.joshuaschnabel.todo.domain.exception.ValidationException;

public record TodoListName(String value) {

    public TodoListName {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("name", "Listenname ist erforderlich");
        }
        value = value.trim();
        if (value.length() > 80) {
            throw new ValidationException("name", "Listenname darf maximal 80 Zeichen lang sein");
        }
    }
}
