package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.entities.AppSession;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.UserDto;
import org.example.service.AppSessionService;
import org.example.service.OpenWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final AppSessionService appSessionService;
    private final OpenWeatherService openWeatherService;

    @Autowired
    public SearchController(AppSessionService appSessionService, OpenWeatherService openWeatherService) {
        this.appSessionService = appSessionService;
        this.openWeatherService = openWeatherService;
    }

    @GetMapping()
    public String showPage(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                           @RequestParam("city") String city,
                           Model model) throws UnsupportedEncodingException, JsonProcessingException {

        UserDto userDto = appSessionService.getUserDtoBySessionId(sessionId);
        model.addAttribute("username", userDto.getLogin());

        model.addAttribute("city", city);
        // TODO: 23/01/2025 обработать ошибки когда город не найден или от API возвращаются 4xx 5xx
        List<LocationResponseDto> locations = openWeatherService.getLocationByCity(city);
        model.addAttribute("locations", locations);

        return "search-results";
    }
}
