package org.example.service;

import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
import org.example.entities.mappers.NewUserDtoToUserMapper;
import org.example.entities.mappers.UserDtoToUserMapper;
import org.example.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    private final NewUserDtoToUserMapper newUserDtoToUserMapper = NewUserDtoToUserMapper.INSTANCE;
    private final UserDtoToUserMapper userDtoToUserMapper = UserDtoToUserMapper.INSTANCE;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Optional<UserDto> findUserByLogin(NewUserDto newUserDto) {
        User userEntity = newUserDtoToUserMapper.toEntity(newUserDto);

        Optional<User> userByLogin = userDao.getUserByLogin(userEntity.getLogin());

        return userByLogin.map(user -> userDtoToUserMapper.toDto(user));
    }

//    public User saveUser(NewUserDto newUserDto) {
//
//    }
}
