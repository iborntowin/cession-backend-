package com.example.cessionappbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            logger.debug("Received JWT: {}", jwt != null ? jwt : "null");

            if (StringUtils.hasText(jwt)) {
                logger.debug("Attempting to validate JWT token");
                boolean isValid = tokenProvider.validateToken(jwt);
                logger.debug("JWT token validation result: {}", isValid);

                if (isValid) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                logger.debug("Valid JWT token found for user: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Get roles from token
                String roles = tokenProvider.getRolesFromJWT(jwt);
                logger.debug("Roles from token: {}", roles);
                
                // Create authentication with authorities from token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in SecurityContext for user: {} with authorities: {}", 
                    username, authentication.getAuthorities());
            } else {
                    logger.debug("JWT token validation failed");
                }
            } else {
                logger.debug("No JWT token found in request");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

