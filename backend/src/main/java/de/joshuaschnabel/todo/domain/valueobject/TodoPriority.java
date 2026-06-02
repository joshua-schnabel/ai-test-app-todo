package de.joshuaschnabel.todo.domain.valueobject;

public enum TodoPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int numericValue;

    TodoPriority(int numericValue) {
        this.numericValue = numericValue;
    }

    public int numericValue() {
        return numericValue;
    }
}
