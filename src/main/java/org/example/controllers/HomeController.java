package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.RemoveLocationDto;
import org.example.entities.dto.UserDto;
import org.example.entities.dto.WeatherDataDto;
import org.example.service.AppSessionService;
import org.example.service.LocationService;
import org.example.service.OpenWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final AppSessionService appSessionService;
    private final LocationService locationService;
    private final OpenWeatherService openWeatherService;

    @Autowired
    public HomeController(AppSessionService appSessionService,
                          LocationService locationService,
                          OpenWeatherService openWeatherService) {
        this.appSessionService = appSessionService;
        this.locationService = locationService;
        this.openWeatherService = openWeatherService;
    }

    @GetMapping()
    public String showPage(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                           Model model) throws JsonProcessingException {

        UserDto userDto = appSessionService.getUserDtoBySessionId(sessionId);
        model.addAttribute("username", userDto.getLogin());

        Map<String, WeatherDataDto> weatherDataMap = openWeatherService.getWeatherForLocationsOf(userDto);
        model.addAttribute("weatherDataMap", weatherDataMap);

        return "home";
    }

    @PostMapping("/add")
    public String addLocation(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                              @ModelAttribute("location") LocationResponseDto locationResponseDto) {

        UserDto userDto = appSessionService.getUserDtoBySessionId(sessionId);
        locationService.saveLocationForUser(locationResponseDto, userDto);

        return "redirect:/home";
    }

    @PostMapping("/delete")
    public String deleteLocation(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                                 @ModelAttribute("removeLocation") RemoveLocationDto removeLocationDto) {

        UserDto userDto = appSessionService.getUserDtoBySessionId(sessionId);
        locationService.removeLocationForUser(removeLocationDto, userDto);

        return "redirect:/home";
    }
}
