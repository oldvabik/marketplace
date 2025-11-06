package org.oldvabik.userservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.oldvabik.userservice.dto.UserCreateDto;
import org.oldvabik.userservice.dto.UserDto;
import org.oldvabik.userservice.dto.UserUpdateDto;
import org.oldvabik.userservice.entity.User;
import org.oldvabik.userservice.exception.AlreadyExistsException;
import org.oldvabik.userservice.exception.NotFoundException;
import org.oldvabik.userservice.mapper.UserMapper;
import org.oldvabik.userservice.repository.UserRepository;
import org.oldvabik.userservice.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        log.info("[UserService] createUser: email={}", dto.getEmail());
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            log.warn("[UserService] createUser: email={} already exists", dto.getEmail());
            throw new AlreadyExistsException("user with email " + dto.getEmail() + " already exists");
        });

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        log.info("[UserService] createUser: user created id={}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserById(Long id) {
        log.debug("[UserService] getUserById: id={}", id);
        User user = userRepository.findByIdWithCards(id)
                .orElseThrow(() -> {
                    log.warn("[UserService] getUserById: user not found id={}", id);
                    return new NotFoundException("user with id " + id + " not found");
                });
        log.info("[UserService] getUserById: found id={}", id);
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Integer page, Integer size) {
        log.debug("[UserService] getAllUsers: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllWithCards(pageable);
        log.info("[UserService] getAllUsers: fetched {} users", users.getContent().size());
        return users.map(userMapper::toDto);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserDto getUserByEmail(String email) {
        log.debug("[UserService] getUserByEmail: email={}", email);
        User user = userRepository.findByEmailWithCards(email)
                .orElseThrow(() -> {
                    log.warn("[UserService] getUserByEmail: user not found email={}", email);
                    return new NotFoundException("user with email " + email + " not found");
                });
        log.info("[UserService] getUserByEmail: found email={}", email);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserDto updateUser(Long id, UserUpdateDto dto) {
        log.info("[UserService] updateUser: id={}", id);
        User user = userRepository.findByIdWithCards(id)
                .orElseThrow(() -> {
                    log.warn("[UserService] updateUser: user not found id={}", id);
                    return new NotFoundException("user with id " + id + " not found");
                });

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    log.warn("[UserService] updateUser: email={} already in use", dto.getEmail());
                    throw new AlreadyExistsException("user with email " + dto.getEmail() + " already exists");
                }
            });
        }

        userMapper.updateEntityFromDto(dto, user);

        if ((dto.getName() != null && !dto.getName().equals(user.getName())) ||
                (dto.getSurname() != null && !dto.getSurname().equals(user.getSurname()))) {
            log.debug("[UserService] updateUser: updating card holders for user id={}", id);
            user.getCards().forEach(card -> card.setHolder(user.getName() + " " + user.getSurname()));
        }

        User saved = userRepository.save(user);
        log.info("[UserService] updateUser: user updated id={}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("[UserService] deleteUser: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[UserService] deleteUser: user not found id={}", id);
                    return new NotFoundException("user with id " + id + " not found");
                });
        userRepository.delete(user);
        log.info("[UserService] deleteUser: deleted id={}", id);
    }
}
