package org.example.service;

import org.example.config.TestConfig;
import org.example.entities.dto.LoginUserDto;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
import org.example.exceptions.EntityAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
class UserRegistrationServiceTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Test
    void testFindUserByLogin_Successful() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);
        assertNotNull(userDto);

        Optional<UserDto> userByLogin = userRegistrationService.findUserByLogin(newUserDto.getLogin());
        assertTrue(userByLogin.isPresent());
    }

    @Test
    void testFindUserByLogin_UserDontExist() {
        Optional<UserDto> userByLogin = userRegistrationService.findUserByLogin("TEST_LOGIN");
        assertTrue(userByLogin.isEmpty());
    }

    @Test
    void testRegisterUser_Successful() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);

        assertNotNull(userDto);
        assertEquals(userDto.getLogin(), newUserDto.getLogin());
    }

    @Test
    void testRegisterUser_LoginNotUnique() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);
        assertNotNull(userDto);

        assertThrows(EntityAlreadyExistsException.class, () -> userRegistrationService.registerUser(newUserDto));
    }

    @Test
    void testVerifyPassword_PasswordVerified() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);
        assertNotNull(userDto);

        LoginUserDto loginUserDto = LoginUserDto.builder()
                .login("testUser")
                .password("test")
                .build();

        assertTrue(userRegistrationService.verifyPassword(loginUserDto));
    }

    @Test
    void testVerifyPassword_WrongPassword() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);
        assertNotNull(userDto);

        LoginUserDto loginUserDto = LoginUserDto.builder()
                .login("testUser")
                .password("12345")
                .build();

        assertFalse(userRegistrationService.verifyPassword(loginUserDto));
    }
}