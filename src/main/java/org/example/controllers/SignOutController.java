package org.example.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.AppSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-out")
public class SignOutController {

    private final AppSessionService appSessionService;

    @Autowired
    public SignOutController(AppSessionService appSessionService) {
        this.appSessionService = appSessionService;
    }

    @PostMapping()
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = AppSessionUtil.getSessionIdFromCookies(request.getCookies());

        if(sessionId != null) {
            appSessionService.deleteSessionById(sessionId);
        }

        Cookie cookie = new Cookie("SESSION_ID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/sign-in";
    }
}
