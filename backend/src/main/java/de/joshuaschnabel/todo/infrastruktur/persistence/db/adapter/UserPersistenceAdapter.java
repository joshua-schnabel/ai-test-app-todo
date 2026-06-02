package de.joshuaschnabel.todo.infrastruktur.persistence.db.adapter;

import de.joshuaschnabel.todo.application.port.out.LoadUserPort;
import de.joshuaschnabel.todo.application.port.out.SaveUserPort;
import de.joshuaschnabel.todo.domain.model.User;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.mapper.UserPersistenceMapper;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final SpringDataUserRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(SpringDataUserRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> loadByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> loadById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }
}
