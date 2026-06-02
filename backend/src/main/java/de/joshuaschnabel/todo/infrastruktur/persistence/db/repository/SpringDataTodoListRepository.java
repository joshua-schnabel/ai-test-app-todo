package de.joshuaschnabel.todo.infrastruktur.persistence.db.repository;

import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.TodoListJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTodoListRepository extends JpaRepository<TodoListJpaEntity, UUID> {

    List<TodoListJpaEntity> findByOwnerId(UUID ownerId);
}
