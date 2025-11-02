package org.oldvabik.marketplace.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.oldvabik.marketplace.dto.CardInfoCreateDto;
import org.oldvabik.marketplace.dto.CardInfoDto;
import org.oldvabik.marketplace.dto.CardInfoUpdateDto;
import org.oldvabik.marketplace.entity.CardInfo;
import org.oldvabik.marketplace.entity.User;
import org.oldvabik.marketplace.exception.AlreadyExistsException;
import org.oldvabik.marketplace.exception.NotFoundException;
import org.oldvabik.marketplace.mapper.CardMapper;
import org.oldvabik.marketplace.repository.CardRepository;
import org.oldvabik.marketplace.repository.UserRepository;
import org.oldvabik.marketplace.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardServiceImpl(CardRepository cardRepository,
                           UserRepository userRepository,
                           CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    @Transactional
    public CardInfoDto createCard(CardInfoCreateDto dto) {
        log.info("[CardService] createCard: userId={}, number={}", dto.getUserId(), dto.getNumber());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.warn("[CardService] createCard: user not found userId={}", dto.getUserId());
                    return new NotFoundException("user with id " + dto.getUserId() + " not found");
                });

        cardRepository.findByNumber(dto.getNumber()).ifPresent(c -> {
            log.warn("[CardService] createCard: card number={} already exists", dto.getNumber());
            throw new AlreadyExistsException("card with number " + dto.getNumber() + " already exists");
        });

        CardInfo card = cardMapper.toEntity(dto);
        card.setUser(user);
        card.setHolder(user.getName() + " " + user.getSurname());

        CardInfo saved = cardRepository.save(card);
        log.info("[CardService] createCard: created id={}", saved.getId());
        return cardMapper.toDto(saved);
    }

    @Override
    public CardInfoDto getCardById(Long id) {
        log.debug("[CardService] getCardById: id={}", id);
        CardInfo card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[CardService] getCardById: not found id={}", id);
                    return new NotFoundException("card with id " + id + " not found");
                });
        log.info("[CardService] getCardById: found id={}", id);
        return cardMapper.toDto(card);
    }

    @Override
    public Page<CardInfoDto> getAllCards(Integer page, Integer size) {
        log.debug("[CardService] getAllCards: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CardInfo> cards = cardRepository.findAll(pageable);
        log.info("[CardService] getAllCards: fetched {} cards", cards.getContent().size());
        return cards.map(cardMapper::toDto);
    }

    @Override
    @Transactional
    public CardInfoDto updateCard(Long id, CardInfoUpdateDto dto) {
        log.info("[CardService] updateCard: id={}", id);
        CardInfo card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[CardService] updateCard: not found id={}", id);
                    return new NotFoundException("card with id " + id + " not found");
                });

        if (dto.getNumber() != null && !dto.getNumber().equals(card.getNumber())) {
            cardRepository.findByNumber(dto.getNumber()).ifPresent(c -> {
                log.warn("[CardService] updateCard: number={} already exists", dto.getNumber());
                throw new AlreadyExistsException("card with number " + dto.getNumber() + " already exists");
            });
        }

        cardMapper.updateEntityFromDto(dto, card);
        CardInfo saved = cardRepository.save(card);
        log.info("[CardService] updateCard: updated id={}", saved.getId());
        return cardMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        log.info("[CardService] deleteCard: id={}", id);
        CardInfo card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[CardService] deleteCard: not found id={}", id);
                    return new NotFoundException("card with id " + id + " not found");
                });
        cardRepository.delete(card);
        log.info("[CardService] deleteCard: deleted id={}", id);
    }
}
