package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.UserDto;
import org.example.exceptions.LocationNotFoundException;
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
@Log4j2
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

        try {
            List<LocationResponseDto> locations = openWeatherService.getLocationByCity(city);
            model.addAttribute("locations", locations);

        } catch (LocationNotFoundException e) {
            throw new LocationNotFoundException("Location '" + city + "' not found.");
        }

        return "search-results";
    }
}
