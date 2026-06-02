package de.joshuaschnabel.todo.infrastruktur.persistence.db.adapter;

import de.joshuaschnabel.todo.application.port.out.DeleteTodoListPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoListPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoListPort;
import de.joshuaschnabel.todo.domain.model.TodoList;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper.TodoListPersistenceMapper;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataTodoListRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TodoListPersistenceAdapter implements LoadTodoListPort, SaveTodoListPort, DeleteTodoListPort {

    private final SpringDataTodoListRepository repository;
    private final TodoListPersistenceMapper mapper;

    public TodoListPersistenceAdapter(SpringDataTodoListRepository repository, TodoListPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TodoList> loadByOwner(UUID ownerId) {
        return repository.findByOwnerId(ownerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TodoList> loadById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public TodoList save(TodoList list) {
        return mapper.toDomain(repository.save(mapper.toEntity(list)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
