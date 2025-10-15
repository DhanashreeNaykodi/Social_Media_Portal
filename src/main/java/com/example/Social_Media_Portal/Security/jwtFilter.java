package com.example.Social_Media_Portal.Security;

import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;


@Component
public class jwtFilter extends OncePerRequestFilter {

    JWTAuth jwtAuth;
    UserRepository userRepository;

    @Autowired
    public jwtFilter(JWTAuth jwtAuth, UserRepository userRepository) {
        this.jwtAuth = jwtAuth;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();

        // Skip JWT validation for public endpoints
        if (requestPath.equals("/users/signup") ||
                requestPath.equals("/users/login") ||
                requestPath.equals("/index/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            sendErrorResponse(response, "Missing or invalid Authorization header. Format: Bearer <token>");
            return;
        }

        try {
            String token = header.substring(7).trim();
            String userEmail = jwtAuth.getUserEmailFromToken(token);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (userRepository.findByEmail(userEmail).isEmpty()) {
                    sendErrorResponse(response, "User not found");
                    return;
                } else {
                    Optional<User> u = userRepository.findByEmail(userEmail);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + u.get().getRole().name());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userEmail,
                                    null,
                                    Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            filterChain.doFilter(request, response);

        }
        catch (MalformedJwtException e) {
            sendErrorResponse(response, "Invalid JWT token format");
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, "JWT token has expired");
        }
        // giving signature based error - wrong signature
        catch (RuntimeException e) {
            sendErrorResponse(response, e.getMessage());
        }
        catch (Exception e) {
            sendErrorResponse(response, "Authentication failed");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String timestamp = LocalDateTime.now().toString();
        String jsonResponse = String.format(
                "{\"timestamp\":\"%s\",\"message\":\"%s\",\"status\":401}",
                timestamp, message
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}