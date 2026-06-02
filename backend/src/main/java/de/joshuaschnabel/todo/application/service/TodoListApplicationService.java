package de.joshuaschnabel.todo.application.service;

import de.joshuaschnabel.todo.application.command.CreateTodoListCommand;
import de.joshuaschnabel.todo.application.command.DeleteTodoListCommand;
import de.joshuaschnabel.todo.application.command.RenameTodoListCommand;
import de.joshuaschnabel.todo.application.exception.ForbiddenException;
import de.joshuaschnabel.todo.application.exception.NotFoundException;
import de.joshuaschnabel.todo.application.port.out.DeleteTodoListPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoListPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoListPort;
import de.joshuaschnabel.todo.application.usecase.CreateTodoListUseCase;
import de.joshuaschnabel.todo.application.usecase.DeleteTodoListUseCase;
import de.joshuaschnabel.todo.application.usecase.GetTodoListsUseCase;
import de.joshuaschnabel.todo.application.usecase.RenameTodoListUseCase;
import de.joshuaschnabel.todo.domain.model.TodoList;
import de.joshuaschnabel.todo.domain.valueobject.TodoListName;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TodoListApplicationService implements CreateTodoListUseCase, RenameTodoListUseCase,
        DeleteTodoListUseCase, GetTodoListsUseCase {

    private final LoadTodoListPort loadTodoListPort;
    private final SaveTodoListPort saveTodoListPort;
    private final DeleteTodoListPort deleteTodoListPort;

    public TodoListApplicationService(LoadTodoListPort loadTodoListPort, SaveTodoListPort saveTodoListPort,
                                      DeleteTodoListPort deleteTodoListPort) {
        this.loadTodoListPort = loadTodoListPort;
        this.saveTodoListPort = saveTodoListPort;
        this.deleteTodoListPort = deleteTodoListPort;
    }

    @Override
    @Transactional
    public TodoList createList(CreateTodoListCommand cmd) {
        Instant now = Instant.now();
        TodoList todoList = new TodoList(
                UUID.randomUUID(),
                cmd.ownerId(),
                new TodoListName(cmd.name()),
                now,
                now
        );
        return saveTodoListPort.save(todoList);
    }

    @Override
    @Transactional
    public TodoList renameList(RenameTodoListCommand cmd) {
        TodoList list = requireOwnedList(cmd.ownerId(), cmd.listId());
        list.rename(new TodoListName(cmd.newName()));
        return saveTodoListPort.save(list);
    }

    @Override
    @Transactional
    public void deleteList(DeleteTodoListCommand cmd) {
        requireOwnedList(cmd.ownerId(), cmd.listId());
        deleteTodoListPort.deleteById(cmd.listId());
    }

    @Override
    public List<TodoList> getLists(UUID ownerId) {
        return loadTodoListPort.loadByOwner(ownerId);
    }

    private TodoList requireOwnedList(UUID ownerId, UUID listId) {
        TodoList list = loadTodoListPort.loadById(listId)
                .orElseThrow(() -> new NotFoundException("Liste wurde nicht gefunden"));
        if (!list.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Kein Zugriff auf diese Liste");
        }
        return list;
    }
}
