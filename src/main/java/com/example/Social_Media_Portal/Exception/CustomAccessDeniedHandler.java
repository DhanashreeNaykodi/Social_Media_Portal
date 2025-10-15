package com.example.Social_Media_Portal.Exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

// for checking right role - if user is not allowed to access other role endpoint (authorization)

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"message\": \"Access denied: If you're seeing this page that means you are not authorized to access this URL.\", \"status\": 403}",
                LocalDateTime.now(),
                request.getRequestURI());
        response.getWriter().write(jsonResponse);




//        Problem -
//        response.getWriter().write() - writes your JSON
//        response.sendError() - clears the buffer and sends default error page
//        response.getWriter().write("{\"error\"" +
//                ": \"Forbidden - You don't have permission\"}");
//        response.sendError(HttpStatus.FORBIDDEN.value(), "If you're seeing this page that means you are not authorized to access this URL.");
    }
}