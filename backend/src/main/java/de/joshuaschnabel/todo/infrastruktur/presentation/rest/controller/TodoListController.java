package de.joshuaschnabel.todo.infrastruktur.presentation.rest.controller;

import de.joshuaschnabel.todo.application.command.CreateTodoListCommand;
import de.joshuaschnabel.todo.application.command.DeleteTodoListCommand;
import de.joshuaschnabel.todo.application.command.RenameTodoListCommand;
import de.joshuaschnabel.todo.application.usecase.CreateTodoListUseCase;
import de.joshuaschnabel.todo.application.usecase.DeleteTodoListUseCase;
import de.joshuaschnabel.todo.application.usecase.GetTodoListsUseCase;
import de.joshuaschnabel.todo.application.usecase.RenameTodoListUseCase;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.mapper.TodoListRestMapper;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.CreateTodoListRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.request.UpdateTodoListRequest;
import de.joshuaschnabel.todo.infrastruktur.presentation.rest.response.TodoListResponse;
import de.joshuaschnabel.todo.infrastruktur.security.adapter.CurrentUserProviderAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists")
public class TodoListController {

    private final GetTodoListsUseCase getTodoListsUseCase;
    private final CreateTodoListUseCase createTodoListUseCase;
    private final RenameTodoListUseCase renameTodoListUseCase;
    private final DeleteTodoListUseCase deleteTodoListUseCase;
    private final CurrentUserProviderAdapter currentUserProviderAdapter;
    private final TodoListRestMapper todoListRestMapper;

    public TodoListController(GetTodoListsUseCase getTodoListsUseCase, CreateTodoListUseCase createTodoListUseCase,
                              RenameTodoListUseCase renameTodoListUseCase,
                              DeleteTodoListUseCase deleteTodoListUseCase,
                              CurrentUserProviderAdapter currentUserProviderAdapter,
                              TodoListRestMapper todoListRestMapper) {
        this.getTodoListsUseCase = getTodoListsUseCase;
        this.createTodoListUseCase = createTodoListUseCase;
        this.renameTodoListUseCase = renameTodoListUseCase;
        this.deleteTodoListUseCase = deleteTodoListUseCase;
        this.currentUserProviderAdapter = currentUserProviderAdapter;
        this.todoListRestMapper = todoListRestMapper;
    }

    @GetMapping
    public ResponseEntity<List<TodoListResponse>> getLists() {
        List<TodoListResponse> response = getTodoListsUseCase.getLists(currentUserProviderAdapter.getCurrentUserId())
                .stream()
                .map(todoListRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TodoListResponse> createList(@Valid @RequestBody CreateTodoListRequest request) {
        TodoListResponse response = todoListRestMapper.toResponse(createTodoListUseCase.createList(
                new CreateTodoListCommand(currentUserProviderAdapter.getCurrentUserId(), request.name())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<TodoListResponse> renameList(@PathVariable UUID listId,
                                                       @Valid @RequestBody UpdateTodoListRequest request) {
        TodoListResponse response = todoListRestMapper.toResponse(renameTodoListUseCase.renameList(
                new RenameTodoListCommand(currentUserProviderAdapter.getCurrentUserId(), listId, request.name())
        ));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable UUID listId) {
        deleteTodoListUseCase.deleteList(new DeleteTodoListCommand(currentUserProviderAdapter.getCurrentUserId(), listId));
        return ResponseEntity.noContent().build();
    }
}
