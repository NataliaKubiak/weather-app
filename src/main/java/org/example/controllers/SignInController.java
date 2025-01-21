package org.example.controllers;

import org.example.entities.dto.LoginUserDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-in")
public class SignInController {

    private final UserService userService;

    @Autowired
    public SignInController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showPage(Model model) {
        model.addAttribute("loginUserDto", new LoginUserDto());

        return "sign-in";
    }

    @PostMapping()
    public String signIn(@ModelAttribute("loginUserDto") LoginUserDto loginUserDto,
                         BindingResult bindingResult) {

        if (!userService.verifyUsername(loginUserDto.getLogin())) {
            bindingResult.rejectValue("login", "user.not.exist", "Invalid username.");
        }

        if (!userService.verifyPassword(loginUserDto)) {
            bindingResult.rejectValue("password", "invalid.password", "Invalid username or password.");
        }

        if (bindingResult.hasErrors()) {
            return "sign-in";  // возвращаем на форму с ошибками
        }

        return "redirect:/index";  // успешная регистрация
    }
}
