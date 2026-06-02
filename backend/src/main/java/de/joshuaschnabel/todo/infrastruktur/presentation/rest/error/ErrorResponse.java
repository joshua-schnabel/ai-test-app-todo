package de.joshuaschnabel.todo.infrastruktur.presentation.rest.error;

public record ErrorResponse(String code, String field, String message) {
}
