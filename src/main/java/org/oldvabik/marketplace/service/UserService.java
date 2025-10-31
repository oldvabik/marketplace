package org.oldvabik.marketplace.service;

import org.oldvabik.marketplace.dto.UserCreateDto;
import org.oldvabik.marketplace.dto.UserDto;
import org.oldvabik.marketplace.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDto createUser(UserCreateDto dto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Integer page, Integer size);

    UserDto getUserByEmail(String email);

    UserDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);
}