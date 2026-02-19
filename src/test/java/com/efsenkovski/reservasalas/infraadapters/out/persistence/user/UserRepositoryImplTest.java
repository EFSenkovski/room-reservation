package com.efsenkovski.reservasalas.infraadapters.out.persistence.user;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryImpl")
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserRepositoryImpl repository;

    @Nested
    @DisplayName("saveUser")
    class SaveUser {

        @Test
        @DisplayName("should save and return mapped user when email is unique")
        void shouldSaveSuccessfully() {
            var user = new User("Alice", "alice@example.com");
            var entity = new UserEntity(user.getExternalId(), user.getName(), user.getEmail(), user.getCreatedAt());

            when(jpaRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(mapper.toEntity(user)).thenReturn(entity);
            when(jpaRepository.save(entity)).thenReturn(entity);
            when(mapper.toUser(entity)).thenReturn(user);

            var result = repository.saveUser(user);

            assertEquals(user, result);
            verify(jpaRepository).save(entity);
        }

        @Test
        @DisplayName("should throw DomainException when email already exists")
        void shouldThrowWhenDuplicateEmail() {
            var user = new User("Alice", "alice@example.com");

            when(jpaRepository.existsByEmail(user.getEmail())).thenReturn(true);

            var exception = assertThrows(DomainException.class, () -> repository.saveUser(user));

            assertTrue(exception.getMessage().contains("alice@example.com"));
            verify(jpaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return mapped list of users")
        void shouldReturnMappedList() {
            var entity1 = new UserEntity(UUID.randomUUID(), "Alice", "alice@example.com", LocalDateTime.now());
            var entity2 = new UserEntity(UUID.randomUUID(), "Bob", "bob@example.com", LocalDateTime.now());
            var user1 = new User(entity1.getExternalId(), "Alice", "alice@example.com", entity1.getCreatedAt());
            var user2 = new User(entity2.getExternalId(), "Bob", "bob@example.com", entity2.getCreatedAt());

            when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
            when(mapper.toUser(entity1)).thenReturn(user1);
            when(mapper.toUser(entity2)).thenReturn(user2);

            var result = repository.findAll();

            assertEquals(2, result.size());
            assertEquals("Alice", result.get(0).getName());
            assertEquals("Bob", result.get(1).getName());
        }
    }

    @Nested
    @DisplayName("findByExternalId")
    class FindByExternalId {

        @Test
        @DisplayName("should return mapped user when found")
        void shouldReturnWhenFound() {
            var externalId = UUID.randomUUID();
            var entity = new UserEntity(externalId, "Alice", "alice@example.com", LocalDateTime.now());
            var user = new User(externalId, "Alice", "alice@example.com", entity.getCreatedAt());

            when(jpaRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
            when(mapper.toUser(entity)).thenReturn(user);

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isPresent());
            assertEquals("Alice", result.get().getName());
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            var externalId = UUID.randomUUID();
            when(jpaRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isEmpty());
        }
    }
}
