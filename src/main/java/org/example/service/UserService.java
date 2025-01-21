package org.example.service;

import org.example.entities.User;
import org.example.entities.dto.LoginUserDto;
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

    @Transactional
    public UserDto registerUser(NewUserDto newUserDto) {
        User user = newUserDtoToUserMapper.toEntity(newUserDto);
        String encodedPassword = passwordEncoder.encode(newUserDto.getPassword());
        user.setEncryptedPassword(encodedPassword);

        userDao.createUser(user);
        return userDtoToUserMapper.toDto(user);
    }

    @Transactional
    public boolean verifyUsername(String username) {
        Optional<User> optionalUser = userDao.getUserByLogin(username);
        return optionalUser.isPresent();
    }

    @Transactional
    public boolean verifyPassword(LoginUserDto loginUserDto) {
        Optional<User> userByLogin = userDao.getUserByLogin(loginUserDto.getLogin());

        if(userByLogin.isEmpty()) {
            return false;
        }

        String encryptedPassword = userByLogin.get().getEncryptedPassword();
        return passwordEncoder.matches(loginUserDto.getPassword(), encryptedPassword);
    }
}
