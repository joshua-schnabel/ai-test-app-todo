package de.joshuaschnabel.todo.infrastruktur.presentation.rest.controller;

import de.joshuaschnabel.todo.application.command.CompleteTodoCommand;
import de.joshuaschnabel.todo.application.command.CreateTodoCommand;
import de.joshuaschnabel.todo.application.command.DeleteTodoCommand;
import de.joshuaschnabel.todo.application.command.ReopenTodoCommand;
import de.joshuaschnabel.todo.application.command.UpdateTodoCommand;
import de.joshuaschnabel.todo.application.exception.NotFoundException;
import de.joshuaschnabel.todo.application.query.GetTodosQuery;
import de.joshuaschnabel.todo.application.usecase.CompleteTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.CreateTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.DeleteTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.GetTodosUseCase;
import de.joshuaschnabel.todo.application.usecase.ReopenTodoUseCase;
import de.joshuaschnabel.todo.application.usecase.UpdateTodoUseCase;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.mapper.TodoRestMapper;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.CreateTodoRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.UpdateTodoRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.TodoResponse;
import de.joshuaschnabel.todo.infrastruktur.security.adapter.CurrentUserProviderAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists/{listId}/todos")
public class TodoController {

    private final GetTodosUseCase getTodosUseCase;
    private final CreateTodoUseCase createTodoUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final CompleteTodoUseCase completeTodoUseCase;
    private final ReopenTodoUseCase reopenTodoUseCase;
    private final CurrentUserProviderAdapter currentUserProviderAdapter;
    private final TodoRestMapper todoRestMapper;

    public TodoController(GetTodosUseCase getTodosUseCase, CreateTodoUseCase createTodoUseCase,
                          UpdateTodoUseCase updateTodoUseCase, DeleteTodoUseCase deleteTodoUseCase,
                          CompleteTodoUseCase completeTodoUseCase, ReopenTodoUseCase reopenTodoUseCase,
                          CurrentUserProviderAdapter currentUserProviderAdapter, TodoRestMapper todoRestMapper) {
        this.getTodosUseCase = getTodosUseCase;
        this.createTodoUseCase = createTodoUseCase;
        this.updateTodoUseCase = updateTodoUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
        this.completeTodoUseCase = completeTodoUseCase;
        this.reopenTodoUseCase = reopenTodoUseCase;
        this.currentUserProviderAdapter = currentUserProviderAdapter;
        this.todoRestMapper = todoRestMapper;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(@PathVariable UUID listId,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String due,
                                                       @RequestParam(required = false) String sort,
                                                       @RequestParam(required = false) String dir) {
        String filter = status != null && !status.isBlank() ? status : due;
        List<TodoResponse> response = getTodosUseCase.getTodos(new GetTodosQuery(
                        currentUserProviderAdapter.getCurrentUserId(),
                        listId,
                        filter,
                        sort,
                        dir
                )).stream()
                .map(todoRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@PathVariable UUID listId,
                                                   @Valid @RequestBody CreateTodoRequest request) {
        TodoResponse response = todoRestMapper.toResponse(createTodoUseCase.createTodo(new CreateTodoCommand(
                currentUserProviderAdapter.getCurrentUserId(),
                listId,
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate()
        )));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable UUID listId, @PathVariable UUID todoId) {
        return ResponseEntity.ok(getTodosUseCase.getTodos(new GetTodosQuery(
                        currentUserProviderAdapter.getCurrentUserId(),
                        listId,
                        null,
                        null,
                        null
                )).stream()
                .filter(todo -> todo.getId().equals(todoId))
                .findFirst()
                .map(todoRestMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Aufgabe wurde nicht gefunden")));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable UUID listId, @PathVariable UUID todoId,
                                                   @Valid @RequestBody UpdateTodoRequest request) {
        TodoResponse response = todoRestMapper.toResponse(updateTodoUseCase.updateTodo(new UpdateTodoCommand(
                currentUserProviderAdapter.getCurrentUserId(),
                listId,
                todoId,
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate()
        )));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID listId, @PathVariable UUID todoId) {
        deleteTodoUseCase.deleteTodo(new DeleteTodoCommand(currentUserProviderAdapter.getCurrentUserId(), listId, todoId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{todoId}/complete")
    public ResponseEntity<TodoResponse> completeTodo(@PathVariable UUID listId, @PathVariable UUID todoId) {
        return ResponseEntity.ok(todoRestMapper.toResponse(completeTodoUseCase.completeTodo(
                new CompleteTodoCommand(currentUserProviderAdapter.getCurrentUserId(), listId, todoId)
        )));
    }

    @PatchMapping("/{todoId}/reopen")
    public ResponseEntity<TodoResponse> reopenTodo(@PathVariable UUID listId, @PathVariable UUID todoId) {
        return ResponseEntity.ok(todoRestMapper.toResponse(reopenTodoUseCase.reopenTodo(
                new ReopenTodoCommand(currentUserProviderAdapter.getCurrentUserId(), listId, todoId)
        )));
    }
}
