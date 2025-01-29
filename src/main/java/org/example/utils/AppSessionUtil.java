package org.example.utils;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppSessionUtil {

    public static String getSessionIdFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
