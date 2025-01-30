package org.example.service;

import org.example.config.TestConfig;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
class UserRegistrationServiceTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Test
    void testRegisterUser() {
        NewUserDto newUserDto = NewUserDto.builder()
                .login("testUser")
                .password("test")
                .repeatPassword("test")
                .build();

        UserDto userDto = userRegistrationService.registerUser(newUserDto);

        assertNotNull(userDto);
        assertEquals(userDto.getLogin(), newUserDto.getLogin());
    }
}