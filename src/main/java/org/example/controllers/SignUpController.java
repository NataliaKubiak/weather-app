package org.example.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
import org.example.service.SessionService;
import org.example.service.UserService;
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

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private final UserService userService;
    private final SessionService sessionService;

    @Autowired
    public SignUpController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping()
    public String showPage(Model model) {
        model.addAttribute("newUserDto", new NewUserDto());

        return "sign-up";
    }

    @PostMapping()
    public String signUp(@ModelAttribute("newUserDto") @Valid NewUserDto newUserDto,
                         BindingResult bindingResult,
                         HttpServletResponse response) {

        if (!Validator.isSamePassword(newUserDto.getPassword(), newUserDto.getRepeatPassword())) {
            bindingResult.rejectValue("repeatPassword", "passwords.not.match", "Passwords don't match.");
        }

        Optional<UserDto> maybeUser = userService.findUserByLogin(newUserDto.getLogin());
        if (maybeUser.isPresent()) {
            bindingResult.rejectValue("login", "user.already.exists", "Account with this username already exists.");
        }

        if (bindingResult.hasErrors()) {
            return "sign-up";  // возвращаем на форму с ошибками
        }

        UserDto registeredUser = userService.registerUser(newUserDto);
        createSessionAndCookie(response, registeredUser.getId());

        return "redirect:/home";  // успешная регистрация
    }

    private void createSessionAndCookie(HttpServletResponse response, int userId) {
        String sessionId = sessionService.createSession(userId);

        Cookie cookie = new Cookie("SESSION_ID", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
