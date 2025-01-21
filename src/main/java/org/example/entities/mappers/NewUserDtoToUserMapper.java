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
    @Mapping(target = "encryptedPassword", ignore = true)
    User toEntity(NewUserDto newUserDto);

    //по моему мне не нужно переводить обратно в NewUserDto - тогда можно и не писать этот метод?
//    NewUserDto toDto(User user);
}
