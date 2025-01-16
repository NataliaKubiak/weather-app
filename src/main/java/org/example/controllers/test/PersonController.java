package org.example.controllers.test;

import org.example.entities.test.Person;
import org.example.service.test.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", personService.showAllPeople());
        return "main/index2";
    }

    @GetMapping("/new")
    public String addPerson(Model model) {
        model.addAttribute("person", new Person());
        return "main/newPerson";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") Person person) {
        personService.savePerson(person);

        return "redirect:/test";
    }
}
