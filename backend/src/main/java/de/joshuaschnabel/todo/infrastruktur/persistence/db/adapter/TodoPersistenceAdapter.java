package de.joshuaschnabel.todo.infrastruktur.persistence.db.adapter;

import de.joshuaschnabel.todo.application.port.out.DeleteTodoPort;
import de.joshuaschnabel.todo.application.port.out.LoadTodoPort;
import de.joshuaschnabel.todo.application.port.out.SaveTodoPort;
import de.joshuaschnabel.todo.domain.model.Todo;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper.TodoPersistenceMapper;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataTodoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TodoPersistenceAdapter implements LoadTodoPort, SaveTodoPort, DeleteTodoPort {

    private final SpringDataTodoRepository repository;
    private final TodoPersistenceMapper mapper;

    public TodoPersistenceAdapter(SpringDataTodoRepository repository, TodoPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Todo> loadByListId(UUID listId) {
        return repository.findByListId(listId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Todo> loadById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Todo save(Todo todo) {
        return mapper.toDomain(repository.save(mapper.toEntity(todo)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
