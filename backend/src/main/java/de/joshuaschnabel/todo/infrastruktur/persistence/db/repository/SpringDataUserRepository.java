package de.joshuaschnabel.todo.infrastruktur.persistence.db.repository;

import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);
}
