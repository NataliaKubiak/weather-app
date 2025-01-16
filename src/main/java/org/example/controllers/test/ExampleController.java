package org.example.controllers.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ppp")
public class ExampleController {

    @GetMapping()
    public String index() {
        return "main/index";
    }
}
