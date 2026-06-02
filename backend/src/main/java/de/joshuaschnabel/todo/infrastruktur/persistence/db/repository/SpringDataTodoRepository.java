package de.joshuaschnabel.todo.infrastruktur.persistence.db.repository;

import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.TodoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTodoRepository extends JpaRepository<TodoJpaEntity, UUID> {

    List<TodoJpaEntity> findByListId(UUID listId);

    void deleteByListId(UUID listId);
}
