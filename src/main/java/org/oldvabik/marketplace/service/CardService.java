package org.oldvabik.marketplace.service;

import org.oldvabik.marketplace.dto.CardInfoCreateDto;
import org.oldvabik.marketplace.dto.CardInfoDto;
import org.oldvabik.marketplace.dto.CardInfoUpdateDto;
import java.util.List;

public interface CardService {
    CardInfoDto createCard(CardInfoCreateDto dto);

    CardInfoDto getCardById(Long id);

    List<CardInfoDto> getAllCards(Integer page, Integer size);

    CardInfoDto updateCard(Long id, CardInfoUpdateDto dto);

    void deleteCard(Long id);
}
