package de.joshuaschnabel.todo.domain.valueobject;

import de.joshuaschnabel.todo.domain.exception.ValidationException;

public record TodoTitle(String value) {

    public TodoTitle {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("title", "Titel ist erforderlich");
        }
        value = value.trim();
        if (value.length() > 120) {
            throw new ValidationException("title", "Titel darf maximal 120 Zeichen lang sein");
        }
    }
}
