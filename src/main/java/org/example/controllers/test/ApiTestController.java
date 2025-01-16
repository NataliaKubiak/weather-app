package org.example.controllers.test;

import org.example.service.test.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
