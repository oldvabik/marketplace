package org.oldvabik.marketplace.unit.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oldvabik.marketplace.dto.*;
import org.oldvabik.marketplace.entity.User;
import org.oldvabik.marketplace.exception.*;
import org.oldvabik.marketplace.mapper.UserMapper;
import org.oldvabik.marketplace.repository.UserRepository;
import org.oldvabik.marketplace.service.impl.UserServiceImpl;
import org.springframework.data.domain.PageImpl;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@example.com");
        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        UserDto userDto = new UserDto();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_emailExists_throwsException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("exists@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void getUserById_found() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByIdWithCards(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(1L);
        assertNotNull(result);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findByIdWithCards(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_returnsList() {
        when(userRepository.findAllWithCards(any())).thenReturn(
                new PageImpl<>(List.of(new User()))
        );
        when(userMapper.toDto(any())).thenReturn(new UserDto());

        var result = userService.getAllUsers(0, 10);
        assertEquals(1, result.size());
    }

    @Test
    void getUserByEmail_found() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByEmailWithCards("email@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserByEmail("email@test.com");
        assertNotNull(result);
    }

    @Test
    void getUserByEmail_notFound() {
        when(userRepository.findByEmailWithCards("email@test.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("email@test.com"));
    }

    @Test
    void updateUser_success() {
        Long id = 1L;
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("new@test.com");
        User user = new User();
        user.setId(id);
        User savedUser = new User();
        savedUser.setId(id);
        UserDto userDto = new UserDto();

        when(userRepository.findByIdWithCards(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(id, dto);
        assertNotNull(result);
    }

    @Test
    void updateUser_emailConflict_throwsException() {
        Long id = 1L;
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("exists@test.com");
        User user = new User();
        user.setId(id);
        User other = new User();
        other.setId(2L);

        when(userRepository.findByIdWithCards(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(other));

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }
}
