package org.oldvabik.userservice.service;

import org.oldvabik.userservice.dto.CardInfoCreateDto;
import org.oldvabik.userservice.dto.CardInfoDto;
import org.oldvabik.userservice.dto.CardInfoUpdateDto;
import org.springframework.data.domain.Page;

public interface CardService {
    CardInfoDto createCard(CardInfoCreateDto dto);

    CardInfoDto getCardById(Long id);

    Page<CardInfoDto> getAllCards(Integer page, Integer size);

    CardInfoDto updateCard(Long id, CardInfoUpdateDto dto);

    void deleteCard(Long id);
}
