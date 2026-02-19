package com.efsenkovski.reservasalas.infraadapters.in.api.user;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.in.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Nested
    @DisplayName("POST /users")
    class CreateUser {

        @Test
        @DisplayName("should return 201 Created with location header")
        void shouldReturn201WithLocation() throws Exception {
            var user = new User(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now());
            when(userService.createUser(eq("John Doe"), eq("john@example.com"))).thenReturn(user);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "John Doe", "email": "john@example.com"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(user.getExternalId().toString()))
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "", "email": "john@example.com"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").value("Name is mandatory!"));
        }

        @Test
        @DisplayName("should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "John Doe", "email": "not-an-email"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }

    @Nested
    @DisplayName("GET /users")
    class GetAllUsers {

        @Test
        @DisplayName("should return 200 with list of users")
        void shouldReturn200WithList() throws Exception {
            var user1 = new User(UUID.randomUUID(), "Alice", "alice@example.com", LocalDateTime.now());
            var user2 = new User(UUID.randomUUID(), "Bob", "bob@example.com", LocalDateTime.now());
            when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Alice"))
                    .andExpect(jsonPath("$[1].name").value("Bob"));
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {

        @Test
        @DisplayName("should return 200 when user found")
        void shouldReturn200WhenFound() throws Exception {
            var externalId = UUID.randomUUID();
            var user = new User(externalId, "John Doe", "john@example.com", LocalDateTime.now());
            when(userService.getUserById(externalId)).thenReturn(user);

            mockMvc.perform(get("/users/{id}", externalId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(externalId.toString()))
                    .andExpect(jsonPath("$.name").value("John Doe"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void shouldReturn404WhenNotFound() throws Exception {
            var externalId = UUID.randomUUID();
            when(userService.getUserById(externalId))
                    .thenThrow(new ResourceNotFoundException("User not found with id: " + externalId));

            mockMvc.perform(get("/users/{id}", externalId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
