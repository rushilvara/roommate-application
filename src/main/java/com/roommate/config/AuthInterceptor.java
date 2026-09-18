package com.roommate.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of("/css/", "/js/", "/images/");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConstants.USER_ID) != null) {
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || "/login".equals(path)
                || "/register".equals(path)
                || "/error".equals(path)
                || PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
