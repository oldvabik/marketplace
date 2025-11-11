package org.oldvabik.userservice.service;

import org.oldvabik.userservice.dto.CardInfoCreateDto;
import org.oldvabik.userservice.dto.CardInfoDto;
import org.oldvabik.userservice.dto.CardInfoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface CardService {
    CardInfoDto createCard(Authentication auth, CardInfoCreateDto dto);

    CardInfoDto getCardById(Authentication auth, Long id);

    Page<CardInfoDto> getAllCards(Integer page, Integer size);

    CardInfoDto updateCard(Authentication auth, Long id, CardInfoUpdateDto dto);

    void deleteCard(Authentication auth, Long id);
}
