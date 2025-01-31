package org.example.controllers.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.example.utils.AppSessionUtil;
import org.example.service.AppSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
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

        log.info("Checking if Session is valid (session id: {})", sessionId);
        if (sessionId != null && appSessionService.isValidSession(sessionId)) {

            log.info("Session is valid (session id: {})", sessionId);
            return true;
        }

        log.info("Session is not valid (session id: {}). Redirecting to '/'auth/sign-in'", sessionId);
        response.sendRedirect("/auth/sign-in");
        return false;
    }
}
