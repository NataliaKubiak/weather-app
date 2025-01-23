package org.example.controllers;

import org.example.entities.AppSession;
import org.example.entities.User;
import org.example.service.AppSessionService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final AppSessionService appSessionService;
    private final UserService userService;

    @Autowired
    public SearchController(AppSessionService appSessionService, UserService userService) {
        this.appSessionService = appSessionService;
        this.userService = userService;
    }

    @GetMapping()
    public String showPage(@CookieValue(value="SESSION_ID", required = false) String sessionId,
                           Model model) {

        AppSession session = appSessionService.getSessionById(sessionId);
        User user = session.getUser();
        model.addAttribute("username", user.getLogin());

        return "search-results";
    }
}
