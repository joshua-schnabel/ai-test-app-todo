package de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper;

import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.domain.valueobject.EmailAddress;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getEmail().value(),
                user.getPasswordHash(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
