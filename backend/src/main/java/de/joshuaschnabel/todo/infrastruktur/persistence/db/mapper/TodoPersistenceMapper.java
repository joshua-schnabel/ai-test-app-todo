package de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper;

import de.joshuaschnabel.todo.domain.model.Todo;
import de.joshuaschnabel.todo.domain.valueobject.TodoPriority;
import de.joshuaschnabel.todo.domain.valueobject.TodoStatus;
import de.joshuaschnabel.todo.domain.valueobject.TodoTitle;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.TodoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TodoPersistenceMapper {

    public Todo toDomain(TodoJpaEntity entity) {
        return new Todo(
                entity.getId(),
                entity.getListId(),
                entity.getOwnerId(),
                new TodoTitle(entity.getTitle()),
                entity.getDescription(),
                TodoStatus.valueOf(entity.getStatus()),
                TodoPriority.valueOf(entity.getPriority()),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TodoJpaEntity toEntity(Todo todo) {
        return new TodoJpaEntity(
                todo.getId(),
                todo.getListId(),
                todo.getOwnerId(),
                todo.getTitle().value(),
                todo.getDescription(),
                todo.getStatus().name(),
                todo.getPriority().name(),
                todo.getDueDate(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
