package org.oldvabik.marketplace.mapper;

import org.mapstruct.*;
import org.oldvabik.marketplace.dto.CardInfoCreateDto;
import org.oldvabik.marketplace.dto.CardInfoDto;
import org.oldvabik.marketplace.dto.CardInfoUpdateDto;
import org.oldvabik.marketplace.entity.CardInfo;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "userId", source = "user.id")
    CardInfoDto toDto(CardInfo entity);

    @Mapping(target = "user.id", source = "userId")
    CardInfo toEntity(CardInfoCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CardInfoUpdateDto dto, @MappingTarget CardInfo entity);
}
