package org.oldvabik.marketplace.mapper;

import org.mapstruct.*;
import org.oldvabik.marketplace.dto.UserCreateDto;
import org.oldvabik.marketplace.dto.UserDto;
import org.oldvabik.marketplace.dto.UserUpdateDto;
import org.oldvabik.marketplace.entity.User;

@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface UserMapper {
    UserDto toDto(User entity);
    User toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget User entity);
}
