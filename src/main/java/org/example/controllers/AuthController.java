package org.example.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.EntityAlreadyExistsException;
import org.example.utils.AppSessionUtil;
import org.example.entities.dto.LoginUserDto;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
import org.example.service.AppSessionService;
import org.example.service.UserRegistrationService;
import org.example.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final AppSessionService appSessionService;

    @Autowired
    public AuthController(UserRegistrationService userRegistrationService, AppSessionService appSessionService) {
        this.userRegistrationService = userRegistrationService;
        this.appSessionService = appSessionService;
    }

    @GetMapping("/sign-up")
    public String showSignUpPage(Model model) {
        model.addAttribute("newUserDto", new NewUserDto());

        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("newUserDto") @Valid NewUserDto newUserDto,
                         BindingResult bindingResult,
                         HttpServletResponse response) {

        if (!Validator.isSamePassword(newUserDto.getPassword(), newUserDto.getRepeatPassword())) {
            bindingResult.rejectValue("repeatPassword", "passwords.not.match", "Passwords don't match.");
        }

        if (bindingResult.hasErrors()) {
              return "sign-up";// возвращаем на форму с ошибками
        }

        try {
            UserDto registeredUser = userRegistrationService.registerUser(newUserDto);
            createSessionAndCookie(response, registeredUser.getId());

        } catch (EntityAlreadyExistsException ex) {
            log.warn(ex.getMessage());

            bindingResult.rejectValue("login", "user.already.exists", ex.getMessage());
            return "sign-up";
        }

        return "redirect:/home";  // успешная регистрация
    }

    @GetMapping("/sign-in")
    public String showSignInPage(Model model) {
        model.addAttribute("loginUserDto", new LoginUserDto());

        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String signIn(@ModelAttribute("loginUserDto") LoginUserDto loginUserDto,
                         BindingResult bindingResult,
                         HttpServletResponse response) {
        Optional<UserDto> maybeUser = userRegistrationService.findUserByLogin(loginUserDto.getLogin());

        if (maybeUser.isEmpty()) {
            bindingResult.rejectValue("login", "user.not.exist", "Invalid username.");
        }

        if (!userRegistrationService.verifyPassword(loginUserDto)) {
            bindingResult.rejectValue("password", "invalid.password", "Invalid username or password.");
        }

        if (bindingResult.hasErrors()) {
            return "sign-in";  // возвращаем на форму с ошибками
        }

        UserDto userDto = maybeUser.get();
        createSessionAndCookie(response, userDto.getId());

        return "redirect:/home";  // успешная регистрация
    }

    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = AppSessionUtil.getSessionIdFromCookies(request.getCookies());

        if(sessionId != null) {
            appSessionService.deleteSessionById(sessionId);
        }

        Cookie cookie = new Cookie("SESSION_ID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/auth/sign-in";
    }

    private void createSessionAndCookie(HttpServletResponse response, int userId) {
        String sessionId = appSessionService.createSession(userId);

        Cookie cookie = new Cookie("SESSION_ID", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
