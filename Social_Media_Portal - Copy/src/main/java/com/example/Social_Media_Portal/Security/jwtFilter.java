package com.example.Social_Media_Portal.Security;

import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class jwtFilter extends OncePerRequestFilter {

    @Autowired
    JWTAuth jwtAuth;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            String userEmail = jwtAuth.getUserEmailFromToken(token);

            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (userRepository.findByEmail(userEmail).isEmpty()) {
                    throw new RuntimeException("User not present in repository");
                } else {
                    Optional<User> u = userRepository.findByEmail(userEmail);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + u.get().getRole().name());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    //u,
                                    userEmail,
                                    null,
                                    Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request,response);

    }
}
