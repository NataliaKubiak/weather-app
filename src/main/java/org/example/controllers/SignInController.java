package org.example.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entities.dto.LoginUserDto;
import org.example.entities.dto.UserDto;
import org.example.service.AppSessionService;
import org.example.service.UserRegistrationService;
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
@RequestMapping("/sign-in")
public class SignInController {

    private final UserRegistrationService userRegistrationService;
    private final AppSessionService appSessionService;

    @Autowired
    public SignInController(UserRegistrationService userRegistrationService, AppSessionService appSessionService) {
        this.userRegistrationService = userRegistrationService;
        this.appSessionService = appSessionService;
    }

    @GetMapping()
    public String showPage(Model model) {
        model.addAttribute("loginUserDto", new LoginUserDto());

        return "sign-in";
    }

    @PostMapping()
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

    private void createSessionAndCookie(HttpServletResponse response, int userId) {
        String sessionId = appSessionService.createSession(userId);

        Cookie cookie = new Cookie("SESSION_ID", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
