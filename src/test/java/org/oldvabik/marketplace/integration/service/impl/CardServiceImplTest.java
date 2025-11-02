package org.oldvabik.marketplace.integration.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oldvabik.marketplace.dto.*;
import org.oldvabik.marketplace.exception.AlreadyExistsException;
import org.oldvabik.marketplace.exception.NotFoundException;
import org.oldvabik.marketplace.repository.CardRepository;
import org.oldvabik.marketplace.repository.UserRepository;
import org.oldvabik.marketplace.service.CardService;
import org.oldvabik.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class CardServiceImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("marketplace")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private CardService cardService;
    @Autowired private UserService userService;
    @Autowired private CardRepository cardRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    private UserDto createTestUser(String email) {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail(email);
        dto.setName("Test");
        dto.setSurname("User");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        return userService.createUser(dto);
    }

    private CardInfoCreateDto cardCreateDto(Long userId, String number) {
        CardInfoCreateDto dto = new CardInfoCreateDto();
        dto.setUserId(userId);
        dto.setNumber(number);
        dto.setExpirationDate(LocalDate.now().plusYears(3)); // обязательное поле
        return dto;
    }

    private CardInfoUpdateDto cardUpdateDto(String number) {
        CardInfoUpdateDto dto = new CardInfoUpdateDto();
        dto.setNumber(number);
        dto.setExpirationDate(LocalDate.now().plusYears(4));
        return dto;
    }

    @Test
    void createCard_success() {
        UserDto user = createTestUser("carduser@example.com");

        CardInfoCreateDto dto = cardCreateDto(user.getId(), "1234567890123456");
        CardInfoDto created = cardService.createCard(dto);

        assertNotNull(created.getId());
        assertEquals("1234567890123456", created.getNumber());
        assertEquals("Test User", created.getHolder());
        assertNotNull(created.getExpirationDate());
    }

    @Test
    void createCard_userNotFound() {
        CardInfoCreateDto dto = cardCreateDto(999L, "1234567890123456");
        assertThrows(NotFoundException.class, () -> cardService.createCard(dto));
    }

    @Test
    void createCard_numberExists() {
        UserDto user = createTestUser("dup@example.com");

        cardService.createCard(cardCreateDto(user.getId(), "duplicate"));
        CardInfoCreateDto dto2 = cardCreateDto(user.getId(), "duplicate");

        assertThrows(AlreadyExistsException.class, () -> cardService.createCard(dto2));
    }

    @Test
    void getCardById_found() {
        UserDto user = createTestUser("get@example.com");
        CardInfoDto created = cardService.createCard(cardCreateDto(user.getId(), "get123"));

        CardInfoDto fetched = cardService.getCardById(created.getId());
        assertEquals("get123", fetched.getNumber());
    }

    @Test
    void getCardById_notFound() {
        assertThrows(NotFoundException.class, () -> cardService.getCardById(999L));
    }

    @Test
    void getAllCards_returnsList() {
        UserDto user = createTestUser("all@example.com");
        cardService.createCard(cardCreateDto(user.getId(), "card1"));
        cardService.createCard(cardCreateDto(user.getId(), "card2"));

        Page<CardInfoDto> page = cardService.getAllCards(0, 10);
        assertEquals(2, page.getContent().size());
    }

    @Test
    void updateCard_success() {
        UserDto user = createTestUser("upd@example.com");
        CardInfoDto created = cardService.createCard(cardCreateDto(user.getId(), "oldnum"));

        CardInfoDto updated = cardService.updateCard(created.getId(),
                cardUpdateDto("newnum"));

        assertEquals("newnum", updated.getNumber());
    }

    @Test
    void updateCard_notFound() {
        assertThrows(NotFoundException.class,
                () -> cardService.updateCard(999L, cardUpdateDto("any")));
    }

    @Test
    void updateCard_numberExists() {
        UserDto user = createTestUser("updexists@example.com");

        cardService.createCard(cardCreateDto(user.getId(), "existing"));
        CardInfoDto toUpdate = cardService.createCard(cardCreateDto(user.getId(), "toupdate"));

        CardInfoUpdateDto dto = cardUpdateDto("existing");
        assertThrows(AlreadyExistsException.class,
                () -> cardService.updateCard(toUpdate.getId(), dto));
    }

    @Test
    void deleteCard_success() {
        UserDto user = createTestUser("del@example.com");
        CardInfoDto created = cardService.createCard(cardCreateDto(user.getId(), "todelete"));

        cardService.deleteCard(created.getId());
        assertFalse(cardRepository.findById(created.getId()).isPresent());
    }

    @Test
    void deleteCard_notFound() {
        assertThrows(NotFoundException.class, () -> cardService.deleteCard(999L));
    }
}