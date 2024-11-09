package com.timebuddy.configurations;

import com.timebuddy.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter intercepts incoming HTTP requests to validate JWT tokens in the Authorization header.
 * If a valid JWT token is present, it authenticates the user and sets the authentication in the SecurityContext.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Constructor for injecting dependencies into the JwtAuthenticationFilter.
     *
     * @param userDetailsService - Service used to load user details by username.
     * @param jwtService - Service used to handle JWT operations such as token validation.
     */
    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * This method filters incoming HTTP requests and checks if the request contains a valid JWT token in the
     * Authorization header. If valid, it authenticates the user and sets the security context.
     *
     * @param request The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param filterChain The filter chain that should be continued after this filter.
     * @throws ServletException If an error occurs during the filtering process.
     * @throws IOException If an input/output error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if the Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            filterChain.doFilter(request, response);  // Continue the filter chain if no JWT token is found
            return;
        }

        try {
            // Extract the JWT token from the Authorization header
            jwt = authHeader.substring(7);  // Remove "Bearer " prefix from the header
            username = jwtService.extractUsername(jwt);  // Extract the username from the JWT token

            // If a username is present and the user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database using the username
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validate the JWT token for the user
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication token for the user
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,  // No credentials needed since the JWT is used for authentication
                                    userDetails.getAuthorities()
                            );
                    // Set the details of the authentication token (e.g., remote address)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication token in the SecurityContext for later use
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // Continue to the next filter in the filter chain
            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            // Log the error and send an Unauthorized response if something goes wrong with token validation
            logger.error("JWT authentication failed", exception);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }
    }
}
