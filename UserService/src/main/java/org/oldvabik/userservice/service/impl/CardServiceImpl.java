package org.oldvabik.userservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.oldvabik.userservice.dto.CardInfoCreateDto;
import org.oldvabik.userservice.dto.CardInfoDto;
import org.oldvabik.userservice.dto.CardInfoUpdateDto;
import org.oldvabik.userservice.entity.CardInfo;
import org.oldvabik.userservice.entity.User;
import org.oldvabik.userservice.exception.AlreadyExistsException;
import org.oldvabik.userservice.exception.NotFoundException;
import org.oldvabik.userservice.mapper.CardMapper;
import org.oldvabik.userservice.repository.CardRepository;
import org.oldvabik.userservice.repository.UserRepository;
import org.oldvabik.userservice.service.CardService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public CardServiceImpl(CardRepository cardRepository,
                           UserRepository userRepository,
                           CardMapper cardMapper,
                           RedisTemplate<String, Object> redisTemplate) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#dto.userId")
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

        redisTemplate.delete("users::" + user.getId());
        redisTemplate.delete("users::" + user.getEmail());

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

        User user = card.getUser();
        redisTemplate.delete("users::" + user.getId());
        redisTemplate.delete("users::" + user.getEmail());

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

        User user = card.getUser();
        redisTemplate.delete("users::" + user.getId());
        redisTemplate.delete("users::" + user.getEmail());

        cardRepository.delete(card);
        log.info("[CardService] deleteCard: deleted id={}", id);
    }
}
