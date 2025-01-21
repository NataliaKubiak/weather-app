package org.example.entities.mappers;

import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewUserDtoToUserMapper {

    NewUserDtoToUserMapper INSTANCE = Mappers.getMapper(NewUserDtoToUserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toEntity(NewUserDto newUserDto);

    NewUserDto toDto(User user);
}
