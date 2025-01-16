package org.example.controllers;

import org.example.entities.Person;
import org.example.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;

@Controller
public class ApiTestController {

    private final ExternalApiService externalApiService;

    @Autowired
    public ApiTestController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/api")
    public String getApiData(Model model) throws UnsupportedEncodingException {
        String data = externalApiService.getWeatherByCity("New York");
        model.addAttribute("data", data);

        return "main/api-data";
    }
}
