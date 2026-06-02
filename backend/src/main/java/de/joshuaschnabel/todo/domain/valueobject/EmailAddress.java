package de.joshuaschnabel.todo.domain.valueobject;

import de.joshuaschnabel.todo.domain.exception.ValidationException;

import java.util.Locale;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public EmailAddress {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("email", "E-Mail ist erforderlich");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ValidationException("email", "Ungültige E-Mail-Adresse");
        }
    }
}
