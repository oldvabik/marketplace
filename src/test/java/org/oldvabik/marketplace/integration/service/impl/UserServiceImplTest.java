package org.oldvabik.marketplace.integration.service.impl;

import org.junit.jupiter.api.*;
import org.oldvabik.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.oldvabik.marketplace.dto.*;
import org.oldvabik.marketplace.entity.User;
import org.oldvabik.marketplace.exception.AlreadyExistsException;
import org.oldvabik.marketplace.exception.NotFoundException;
import org.oldvabik.marketplace.repository.UserRepository;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("marketplace")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_success() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@example.com");
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));

        UserDto saved = userService.createUser(dto);

        assertNotNull(saved.getId());
        assertEquals("test@example.com", saved.getEmail());
    }

    @Test
    void createUser_emailExists_throwsException() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setName("Exists");
        user.setSurname("User");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(user);

        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("exists@example.com");
        dto.setName("New");
        dto.setSurname("User");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void getUserById_found() {
        User user = new User();
        user.setEmail("get@example.com");
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        User saved = userRepository.save(user);

        UserDto dto = userService.getUserById(saved.getId());

        assertEquals("get@example.com", dto.getEmail());
    }

    @Test
    void getUserById_notFound() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsers_returnsList() {
        User user1 = new User();
        user1.setName("A");
        user1.setSurname("B");
        user1.setEmail("a@b.com");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("C");
        user2.setSurname("D");
        user2.setEmail("c@d.com");
        user2.setBirthDate(LocalDate.of(1991, 2, 2));
        userRepository.save(user2);

        Page<UserDto> page = userService.getAllUsers(0, 10);

        assertEquals(2, page.getContent().size());
    }

    @Test
    void getUserByEmail_found() {
        User user = new User();
        user.setEmail("email@test.com");
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(user);

        UserDto dto = userService.getUserByEmail("email@test.com");

        assertEquals("email@test.com", dto.getEmail());
    }

    @Test
    void getUserByEmail_notFound() {
        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("notfound@test.com"));
    }

    @Test
    void updateUser_success() {
        User user = new User();
        user.setEmail("old@test.com");
        user.setName("Old");
        user.setSurname("Name");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        User saved = userRepository.save(user);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("new@test.com");
        dto.setName("New");
        dto.setSurname("Name");

        UserDto updated = userService.updateUser(saved.getId(), dto);

        assertEquals("new@test.com", updated.getEmail());
        assertEquals("New", updated.getName());
    }

    @Test
    void updateUser_emailConflict_throwsException() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setName("User");
        user1.setSurname("One");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("User");
        user2.setSurname("Two");
        user2.setBirthDate(LocalDate.of(1991, 2, 2));
        userRepository.save(user2);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("user2@example.com");

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(savedUser1.getId(), dto));
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setEmail("delete@test.com");
        user.setName("Delete");
        user.setSurname("User");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        User saved = userRepository.save(user);

        userService.deleteUser(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteUser_notFound() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }
}