package org.oldvabik.userservice.mapper;

import org.mapstruct.*;
import org.oldvabik.userservice.dto.CardInfoCreateDto;
import org.oldvabik.userservice.dto.CardInfoDto;
import org.oldvabik.userservice.dto.CardInfoUpdateDto;
import org.oldvabik.userservice.entity.CardInfo;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "userId", source = "user.id")
    CardInfoDto toDto(CardInfo entity);

    @Mapping(target = "user.id", source = "userId")
    CardInfo toEntity(CardInfoCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CardInfoUpdateDto dto, @MappingTarget CardInfo entity);
}
