package de.joshuaschnabel.todo.application.service;

import de.joshuaschnabel.todo.application.command.CompleteTodoCommand;
import de.joshuaschnabel.todo.application.command.CreateTodoCommand;
import de.joshuaschnabel.todo.application.command.DeleteTodoCommand;
import de.joshuaschnabel.todo.application.command.ReopenTodoCommand;
import de.joshuaschnabel.todo.application.command.UpdateTodoCommand;
import de.joshuaschnabel.todo.application.exception.ForbiddenException;
import de.joshuaschnabel.todo.application.exception.NotFoundException;
import de.joshuaschnabel.todo.application.port.out.DeleteTodoPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoListPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoPort;
import de.joshuaschnabel.todo.application.query.GetTodosQuery;
import de.joshuaschnabel.todo.application.usecase.CompleteTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.CreateTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.DeleteTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.GetTodosUseCase;
import de.joshuaschnabel.todo.application.usecase.ReopenTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.UpdateTodoUseCase;
import de.joshuaschnabel.todo.domain.model.Todo;
import de.joshuaschnabel.todo.domain.model.TodoList;
import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;
import de.joshuaschnabel.todo.domain.valueobject.TodoStatus;
import de.joshuaschnabel.todo.domain.valueobject.TodoTitle;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TodoApplicationService implements CreateTodoUseCase, UpdateTodoUseCase, DeleteTodoUseCase,
        CompleteTodoUseCase, ReopenTodoUseCase, GetTodosUseCase {

    private final LoadTodoListPort loadTodoListPort;
    private final LoadTodoPort loadTodoPort;
    private final SaveTodoPort saveTodoPort;
    private final DeleteTodoPort deleteTodoPort;

    public TodoApplicationService(LoadTodoListPort loadTodoListPort, LoadTodoPort loadTodoPort,
                                  SaveTodoPort saveTodoPort, DeleteTodoPort deleteTodoPort) {
        this.loadTodoListPort = loadTodoListPort;
        this.loadTodoPort = loadTodoPort;
        this.saveTodoPort = saveTodoPort;
        this.deleteTodoPort = deleteTodoPort;
    }

    @Override
    @Transactional
    public Todo createTodo(CreateTodoCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        Instant now = Instant.now();
        Todo todo = new Todo(
                UUID.randomUUID(),
                cmd.listId(),
                cmd.ownerId(),
                new TodoTitle(cmd.title()),
                normalizeDescription(cmd.description()),
                TodoStatus.OPEN,
                Objects.requireNonNullElse(cmd.priority(), TodoPriority.MEDIUM),
                cmd.dueDate(),
                now,
                now
        );
        return saveTodoPort.save(todo);
    }

    @Override
    @Transactional
    public Todo updateTodo(UpdateTodoCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        Todo todo = requireTodo(cmd.todoId(), cmd.listId());
        todo.update(
                new TodoTitle(cmd.title()),
                normalizeDescription(cmd.description()),
                Objects.requireNonNullElse(cmd.priority(), TodoPriority.MEDIUM),
                cmd.dueDate()
        );
        return saveTodoPort.save(todo);
    }

    @Override
    @Transactional
    public void deleteTodo(DeleteTodoCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        requireTodo(cmd.todoId(), cmd.listId());
        deleteTodoPort.deleteById(cmd.todoId());
    }

    @Override
    @Transactional
    public Todo completeTodo(CompleteTodoCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        Todo todo = requireTodo(cmd.todoId(), cmd.listId());
        todo.complete();
        return saveTodoPort.save(todo);
    }

    @Override
    @Transactional
    public Todo reopenTodo(ReopenTodoCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        Todo todo = requireTodo(cmd.todoId(), cmd.listId());
        todo.reopen();
        return saveTodoPort.save(todo);
    }

    @Override
    public List<Todo> getTodos(GetTodosQuery query) {
        requireOwnedList(query.ownerId(), query.listId());
        LocalDate today = LocalDate.now();
        return loadTodoPort.loadByListId(query.listId()).stream()
                .filter(todo -> filterByStatus(todo, query.statusFilter(), today))
                .sorted(resolveComparator(query.sortBy(), query.sortDir()))
                .toList();
    }

    private TodoList requireOwnedList(UUID ownerId, UUID listId) {
        TodoList list = loadTodoListPort.loadById(listId)
                .orElseThrow(() -> new NotFoundException("Liste wurde nicht gefunden"));
        if (!list.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Kein Zugriff auf diese Liste");
        }
        return list;
    }

    private Todo requireTodo(UUID todoId, UUID listId) {
        Todo todo = loadTodoPort.loadById(todoId)
                .orElseThrow(() -> new NotFoundException("Aufgabe wurde nicht gefunden"));
        if (!todo.getListId().equals(listId)) {
            throw new NotFoundException("Aufgabe wurde nicht gefunden");
        }
        return todo;
    }

    private boolean filterByStatus(Todo todo, String statusFilter, LocalDate today) {
        if (statusFilter == null || statusFilter.isBlank()) {
            return true;
        }
        return switch (statusFilter.trim().toLowerCase()) {
            case "open" -> todo.getStatus() == TodoStatus.OPEN;
            case "done" -> todo.getStatus() == TodoStatus.DONE;
            case "today" -> today.equals(todo.getDueDate());
            case "overdue" -> todo.getDueDate() != null && todo.getDueDate().isBefore(today)
                    && todo.getStatus() == TodoStatus.OPEN;
            default -> true;
        };
    }

    private Comparator<Todo> resolveComparator(String sortBy, String sortDir) {
        Comparator<Todo> comparator;
        if (sortBy == null || sortBy.isBlank()) {
            comparator = Comparator
                    .comparing((Todo todo) -> todo.getStatus() == TodoStatus.DONE)
                    .thenComparing(Todo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing((Todo todo) -> todo.getPriority().numericValue(), Comparator.reverseOrder())
                    .thenComparing(Todo::getCreatedAt);
        } else {
            comparator = switch (sortBy.trim()) {
                case "dueDate" -> Comparator.comparing(Todo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
                case "priority" -> Comparator.comparing((Todo todo) -> todo.getPriority().numericValue());
                case "createdAt" -> Comparator.comparing(Todo::getCreatedAt);
                case "status" -> Comparator.comparing((Todo todo) -> todo.getStatus() == TodoStatus.DONE);
                default -> Comparator.comparing(Todo::getCreatedAt);
            };
            if ("priority".equals(sortBy.trim()) && !"asc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
        }
        if (sortBy != null && !sortBy.isBlank() && "desc".equalsIgnoreCase(sortDir) && !"priority".equals(sortBy.trim())) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
