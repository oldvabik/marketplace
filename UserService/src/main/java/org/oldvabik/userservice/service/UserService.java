package org.oldvabik.userservice.service;

import org.oldvabik.userservice.dto.UserCreateDto;
import org.oldvabik.userservice.dto.UserDto;
import org.oldvabik.userservice.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDto createUser(UserCreateDto dto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Integer page, Integer size);

    UserDto getUserByEmail(String email);

    UserDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);
}