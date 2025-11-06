package org.oldvabik.userservice.unit.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oldvabik.userservice.dto.*;
import org.oldvabik.userservice.entity.*;
import org.oldvabik.userservice.exception.*;
import org.oldvabik.userservice.mapper.CardMapper;
import org.oldvabik.userservice.repository.*;
import org.oldvabik.userservice.service.impl.CardServiceImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardMapper cardMapper;
    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_success() {
        CardInfoCreateDto dto = new CardInfoCreateDto();
        dto.setUserId(1L);
        dto.setNumber("1234");
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        CardInfo card = new CardInfo();
        CardInfo saved = new CardInfo();
        saved.setId(1L);
        CardInfoDto cardDto = new CardInfoDto();

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
        when(cardRepository.findByNumber(dto.getNumber())).thenReturn(Optional.empty());
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(saved);
        when(cardMapper.toDto(saved)).thenReturn(cardDto);

        CardInfoDto result = cardService.createCard(dto);
        assertNotNull(result);
    }

    @Test
    void createCard_userNotFound() {
        CardInfoCreateDto dto = new CardInfoCreateDto();
        dto.setUserId(1L);
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.createCard(dto));
    }

    @Test
    void createCard_numberExists() {
        CardInfoCreateDto dto = new CardInfoCreateDto();
        dto.setUserId(1L);
        dto.setNumber("1234");
        User user = new User();
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
        when(cardRepository.findByNumber(dto.getNumber())).thenReturn(Optional.of(new CardInfo()));
        assertThrows(AlreadyExistsException.class, () -> cardService.createCard(dto));
    }

    @Test
    void getCardById_found() {
        CardInfo card = new CardInfo();
        CardInfoDto dto = new CardInfoDto();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(dto);
        assertNotNull(cardService.getCardById(1L));
    }

    @Test
    void getCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.getCardById(1L));
    }

    @Test
    void getAllCards_returnsList() {
        when(cardRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new CardInfo())));
        when(cardMapper.toDto(any(CardInfo.class))).thenReturn(new CardInfoDto());

        var result = cardService.getAllCards(0, 10);
        assertEquals(1, result.getContent().size());
    }


    @Test
    void updateCard_success() {
        Long id = 1L;
        CardInfoUpdateDto dto = new CardInfoUpdateDto();
        dto.setNumber("5678");
        CardInfo card = new CardInfo();
        CardInfo saved = new CardInfo();
        saved.setId(id);
        CardInfoDto cardDto = new CardInfoDto();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.findByNumber(dto.getNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(card)).thenReturn(saved);
        when(cardMapper.toDto(saved)).thenReturn(cardDto);

        assertNotNull(cardService.updateCard(id, dto));
    }

    @Test
    void updateCard_notFound() {
        Long id = 999L;
        CardInfoUpdateDto dto = new CardInfoUpdateDto();
        dto.setNumber("any");

        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.updateCard(id, dto));
    }

    @Test
    void updateCard_numberExists() {
        Long id = 1L;
        CardInfoUpdateDto dto = new CardInfoUpdateDto();
        dto.setNumber("1234");
        CardInfo card = new CardInfo();
        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.findByNumber(dto.getNumber())).thenReturn(Optional.of(new CardInfo()));
        assertThrows(AlreadyExistsException.class, () -> cardService.updateCard(id, dto));
    }

    @Test
    void deleteCard_success() {
        CardInfo card = new CardInfo();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        cardService.deleteCard(1L);
        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.deleteCard(1L));
    }
}
