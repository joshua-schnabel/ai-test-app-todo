package de.joshuaschnabel.todo.domain.model;

import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;
import de.joshuaschnabel.todo.domain.valueobject.TodoStatus;
import de.joshuaschnabel.todo.domain.valueobject.TodoTitle;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Todo {

    private final UUID id;
    private final UUID listId;
    private final UUID ownerId;
    private TodoTitle title;
    private String description;
    private TodoStatus status;
    private TodoPriority priority;
    private LocalDate dueDate;
    private final Instant createdAt;
    private Instant updatedAt;

    public Todo(UUID id, UUID listId, UUID ownerId, TodoTitle title, String description, TodoStatus status,
                TodoPriority priority, LocalDate dueDate, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.listId = Objects.requireNonNull(listId, "listId must not be null");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.priority = Objects.requireNonNull(priority, "priority must not be null");
        this.dueDate = dueDate;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public void update(TodoTitle title, String description, TodoPriority priority, LocalDate dueDate) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.priority = Objects.requireNonNull(priority, "priority must not be null");
        this.dueDate = dueDate;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        this.status = TodoStatus.DONE;
        this.updatedAt = Instant.now();
    }

    public void reopen() {
        this.status = TodoStatus.OPEN;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getListId() {
        return listId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public TodoTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
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
        if (!(o instanceof Todo todo)) {
            return false;
        }
        return id.equals(todo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
