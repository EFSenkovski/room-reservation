package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private String name;
    private String email;

    @BeforeEach
    void setUp() {
        name = "Eduardo Felipe";
        email = "eduardosenkovski@gmail.com";
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {
        @Test
        @DisplayName("should create a user successfully when data is valid")
        void shouldCreateAUserSuccessfully() {
            when(userRepo.saveUser(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var result =  userServiceImpl.createUser(name, email);

            assertNotNull(result, "the result should not be null");
            assertEquals(name, result.getName(), "the name should match");
            assertEquals(email, result.getEmail(), "the email should match");

            verify(userRepo).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {
        @Test
        @DisplayName("should return empty list when no users exist")
        void shouldReturnEmptyList() {
            when(userRepo.findAll()).thenReturn(List.of());

            var result = userServiceImpl.getAllUsers();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userRepo).findAll();
        }

        @Test
        @DisplayName("should return list of users when users exist")
        void shouldReturnListOfUsers() {
            var user1 = new User("Eduardo Felipe", "eduardo@email.com");
            var user2 = new User("John Doe", "john@email.com");
            when(userRepo.findAll()).thenReturn(List.of(user1, user2));

            var result = userServiceImpl.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Eduardo Felipe", result.get(0).getName());
            assertEquals("John Doe", result.get(1).getName());
            verify(userRepo).findAll();
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {
        @Test
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() {
            var user = new User(name, email);
            var externalId = user.getExternalId();
            when(userRepo.findByExternalId(externalId)).thenReturn(Optional.of(user));

            var result = userServiceImpl.getUserById(externalId);

            assertNotNull(result);
            assertEquals(name, result.getName());
            assertEquals(email, result.getEmail());
            verify(userRepo).findByExternalId(externalId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            var externalId = UUID.randomUUID();
            when(userRepo.findByExternalId(externalId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userServiceImpl.getUserById(externalId));

            verify(userRepo).findByExternalId(externalId);
        }
    }
}