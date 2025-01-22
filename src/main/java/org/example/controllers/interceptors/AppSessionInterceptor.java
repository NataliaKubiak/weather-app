package org.example.controllers.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controllers.AppSessionUtil;
import org.example.service.AppSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AppSessionInterceptor implements HandlerInterceptor {

    private final AppSessionService appSessionService;

    @Autowired
    public AppSessionInterceptor(AppSessionService appSessionService) {
        this.appSessionService = appSessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionId = AppSessionUtil.getSessionIdFromCookies(request.getCookies());

        if (sessionId != null && appSessionService.isValidSession(sessionId)) {
            return true;
        }

        response.sendRedirect("/sign-in");
        return false;
    }
}
