package org.example.entities.mappers;

import org.example.entities.User;
import org.example.entities.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDtoToUserMapper {

    UserDtoToUserMapper INSTANCE = Mappers.getMapper(UserDtoToUserMapper.class);

    @Mapping(target = "encryptedPassword", ignore = true)
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
