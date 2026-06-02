package de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper;

import de.joshuaschnabel.todo.domain.model.TodoList;
import de.joshuaschnabel.todo.domain.valueobject.TodoListName;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.TodoListJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TodoListPersistenceMapper {

    public TodoList toDomain(TodoListJpaEntity entity) {
        return new TodoList(
                entity.getId(),
                entity.getOwnerId(),
                new TodoListName(entity.getName()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TodoListJpaEntity toEntity(TodoList todoList) {
        return new TodoListJpaEntity(
                todoList.getId(),
                todoList.getOwnerId(),
                todoList.getName().value(),
                todoList.getCreatedAt(),
                todoList.getUpdatedAt()
        );
    }
}
