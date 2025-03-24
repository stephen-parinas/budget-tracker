package com.stephen_parinas.budget_tracker.config;

import com.stephen_parinas.budget_tracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * A security filter that intercepts incoming HTTP requests to validate JWT tokens.
 * If a valid token is found, it sets the authentication context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Filters incoming HTTP requests to handle JWT authentication.
     * This method extracts the JWT token from the Authorisation header, validates it,
     * and sets up authentication in the SecurityContext if the token is valid.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain to pass the request along.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Retrieve the authorisation header from the request
        final String authHeader = request.getHeader("Authorization");

        // If the authorisation header is not valid, continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract the JWT token by removing the "Bearer " prefix
            final String jwt = authHeader.substring(7);

            // Extract the username from the JWT token
            final String username = jwtService.extractUsername(jwt);

            // Proceed only if a username was extracted and no authentication is currently set
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validate the JWT token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication token with the user details and their authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Set additional request details, then set the authenticated user in the security context
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
