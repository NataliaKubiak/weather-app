package org.example.controllers;

import jakarta.validation.Valid;
import org.example.entities.User;
import org.example.entities.dto.NewUserDto;
import org.example.entities.test.Person;
import org.example.utils.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SignUpController {

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

        if (bindingResult.hasErrors()) {
            return "sign-up";  // возвращаем на форму с ошибками
        }

        return "redirect:/index";  // успешная регистрация
    }
}
