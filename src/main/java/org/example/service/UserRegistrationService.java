package org.example.service;

import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Service
public class UserRegistrationService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    private final NewUserDtoToUserMapper newUserDtoToUserMapper = NewUserDtoToUserMapper.INSTANCE;
    private final UserDtoToUserMapper userDtoToUserMapper = UserDtoToUserMapper.INSTANCE;

    @Autowired
    public UserRegistrationService(UserDao userDao) {
        this.userDao = userDao;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Optional<UserDto> findUserByLogin(String login) {
        log.info("Looking for a user with login: {}", login);

        Optional<User> userByLogin = userDao.getUserByLogin(login);

        return userByLogin.map(user -> userDtoToUserMapper.toDto(user));
    }

    @Transactional
    public UserDto registerUser(NewUserDto newUserDto) {
        log.info("Registering a new user: {}", newUserDto.getLogin());

        User user = newUserDtoToUserMapper.toEntity(newUserDto);
        String encodedPassword = passwordEncoder.encode(newUserDto.getPassword());
        user.setEncryptedPassword(encodedPassword);

        userDao.createUser(user);
        log.info("User successfully registered: {}", newUserDto.getLogin());

        return userDtoToUserMapper.toDto(user);
    }

    @Transactional
    public boolean verifyPassword(LoginUserDto loginUserDto) {
        log.info("Verifying password for user: {}", loginUserDto.getLogin());

        Optional<User> userByLogin = userDao.getUserByLogin(loginUserDto.getLogin());

        if(userByLogin.isEmpty()) {
            log.warn("User not found during password verification: {}", loginUserDto.getLogin());
            return false;
        }

        String encryptedPassword = userByLogin.get().getEncryptedPassword();
        boolean isMatch = passwordEncoder.matches(loginUserDto.getPassword(), encryptedPassword);

        if (isMatch) {
            log.info("Password verified successfully for user: {}", loginUserDto.getLogin());
        } else {
            log.warn("Password verification failed for user: {}", loginUserDto.getLogin());
        }

        return isMatch;
    }
}
