package com.timebuddy.configurations;

import com.timebuddy.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Konstruktor som injicerar nödvändiga beroenden för JWT-autentisering.
     *
     * @param userDetailsService - Tjänst för att hämta användardetaljer.
     * @param jwtService - Tjänst för att hantera JWT-token.
     */
    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Filters incoming HTTP requests to check for a valid JWT token in the Authorization header.
     * If a valid token is found, it authenticates the user and sets the authentication in the SecurityContext.
     *
     * @param request The HTTP request to be processed.
     * @param response The HTTP response to be sent.
     * @param filterChain The filter chain to which the request should continue after processing.
     * @throws ServletException If an error occurs during the filtering process.
     * @throws IOException If an I/O error occurs while processing the request or response.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if the header is not null and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If the Authorization header is not in the expected format, pass the request along the filter chain
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract the JWT token from the header and obtain the username from the token
            jwt = authHeader.substring(7);  // Remove "Bearer " from the header
            username = jwtService.extractUsername(jwt);

            // If the username is present and the user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Retrieve user details from the database using the username
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Check if the JWT token is valid
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication token and set it in the SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    // Set the details of the authentication token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication token in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // Continue to the next filter in the chain
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // If an error occurs (e.g., invalid JWT token), send an Unauthorized error response
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }
    }
}

