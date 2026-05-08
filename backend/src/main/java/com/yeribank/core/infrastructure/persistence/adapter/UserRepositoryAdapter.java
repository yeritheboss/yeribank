package com.yeribank.core.infrastructure.persistence.adapter;

import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.User;
import com.yeribank.core.infrastructure.persistence.entity.UserJpaEntity;
import com.yeribank.core.infrastructure.persistence.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

  private final UserJpaRepository repository;

  public UserRepositoryAdapter(UserJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email).map(this::toDomain);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public long countUsers() {
    return repository.count();
  }

  @Override
  public List<User> findAll() {
    return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
  }

  @Override
  public User save(User user) {
    UserJpaEntity entity = toEntity(user);
    return toDomain(repository.save(entity));
  }

  private User toDomain(UserJpaEntity entity) {
    return new User(
        entity.getId(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.getRole(),
        entity.getFullName(),
        entity.getAge(),
        entity.getJobTitle(),
        entity.getCreatedAt());
  }

  private UserJpaEntity toEntity(User user) {
    UserJpaEntity entity = new UserJpaEntity();
    entity.setId(user.id());
    entity.setEmail(user.email());
    entity.setPasswordHash(user.passwordHash());
    entity.setRole(user.role());
    entity.setFullName(user.fullName());
    entity.setAge(user.age());
    entity.setJobTitle(user.jobTitle());
    entity.setCreatedAt(user.createdAt());
    return entity;
  }
}
