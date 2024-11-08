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
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, @Lazy JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Filtreringsmetod som körs vid varje begäran för att kontrollera JWT-token och autentisera användaren om token är giltig.
     *
     * @param request - HTTP-begäran som innehåller JWT-token i Authorization-headern.
     * @param response - HTTP-svar som används för att skicka felmeddelanden om något går snett.
     * @param filterChain - Filterkedja som innehåller andra filter att köra efter detta.
     * @throws ServletException - Om ett fel inträffar vid filtrering.
     * @throws IOException - Om ett IO-fel uppstår.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Hämta Authorization-headern från begäran
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Kontrollera att headern inte är tom och att den börjar med "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrahera JWT-token och hämta användarnamnet från den
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);

            // Om användarnamnet finns och användaren inte redan är autentiserad
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Hämta användardetaljer från databasen
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Kontrollera om token är giltig
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Skapa en autentiseringstoken och sätt den i SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // Fortsätt till nästa filter i kedjan
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // Skicka felmeddelande om JWT-token är ogiltig
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }
    }

}
