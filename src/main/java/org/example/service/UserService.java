package org.example.service;

import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.example.entities.mappers.NewUserDtoToUserMapper;
import org.example.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final NewUserDtoToUserMapper newUserDtoToUserMapper = NewUserDtoToUserMapper.INSTANCE;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public Optional<User> getUser(NewUserDto newUserDto) {
        User userEntity = newUserDtoToUserMapper.toEntity(newUserDto);

        return userDao.getUserByLogin(userEntity.getLogin());
    }
}
