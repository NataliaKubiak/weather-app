package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.entities.AppSession;
import org.example.entities.User;
import org.example.entities.dto.LocationResponseDto;
import org.example.entities.dto.RemoveLocationDto;
import org.example.entities.dto.WeatherDataDto;
import org.example.service.AppSessionService;
import org.example.service.LocationService;
import org.example.service.OpenWeatherService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        AppSession session = appSessionService.getSessionById(sessionId);
        User user = session.getUser();
        model.addAttribute("username", user.getLogin());

        List<WeatherDataDto> weatherDataList = openWeatherService.getWeatherForLocationsOf(user);
        model.addAttribute("weatherDataList", weatherDataList);

        return "home";
    }

    @PostMapping("/add")
    public String addLocation(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                              @ModelAttribute("location") LocationResponseDto locationResponseDto) {

        User user = appSessionService.getSessionById(sessionId).getUser();
        locationService.saveLocationForUser(locationResponseDto, user);

        return "redirect:/home";
    }

    @PostMapping("/delete")
    public String deleteLocation(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                                 @ModelAttribute("removeLocation")RemoveLocationDto removeLocationDto) {

        User user = appSessionService.getSessionById(sessionId).getUser();
        locationService.removeLocationForUser(removeLocationDto, user);

        return "redirect:/home";
    }
}
