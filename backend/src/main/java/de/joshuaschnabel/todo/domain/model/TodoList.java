package de.joshuaschnabel.todo.domain.model;

import de.joshuaschnabel.todo.domain.valueobject.TodoListName;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TodoList {

    private final UUID id;
    private final UUID ownerId;
    private TodoListName name;
    private final Instant createdAt;
    private Instant updatedAt;

    public TodoList(UUID id, UUID ownerId, TodoListName name, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public void rename(TodoListName newName) {
        this.name = Objects.requireNonNull(newName, "newName must not be null");
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public TodoListName getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TodoList todoList)) {
            return false;
        }
        return id.equals(todoList.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
