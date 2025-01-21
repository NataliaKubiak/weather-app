package org.example.controllers;

import jakarta.validation.Valid;
import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.example.entities.dto.UserDto;
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

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showPage(Model model) {
        model.addAttribute("newUserDto", new NewUserDto());

        return "sign-up";
    }

    @PostMapping()
    public String signUp(@ModelAttribute("newUserDto") @Valid NewUserDto newUserDto,
                         BindingResult bindingResult) {

        if (!Validator.isSamePassword(newUserDto.getPassword(), newUserDto.getRepeatPassword())) {
            bindingResult.rejectValue("repeatPassword", "passwords.not.match", "Passwords don't match.");
        }

        Optional<UserDto> maybeUser = userService.findUserByLogin(newUserDto);
        if (maybeUser.isPresent()) {
            bindingResult.rejectValue("login", "user.already.exists", "Account with this username already exists.");
        }

        if (bindingResult.hasErrors()) {
            return "sign-up";  // возвращаем на форму с ошибками
        }

        UserDto registeredUser = userService.registerUser(newUserDto);

        return "redirect:/index";  // успешная регистрация
    }
}
