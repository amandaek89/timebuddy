package com.timebuddy.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor to inject the JwtAuthenticationFilter.
     *
     * @param jwtAuthenticationFilter - The filter that handles JWT authentication for each request.
     */
    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures HTTP security settings for the application.
     * Disables CSRF protection, enables CORS with default settings, and applies security rules
     * for different URL patterns.
     *
     * @param httpSecurity - Object that allows configuration of web-based security for specific HTTP requests.
     * @return A SecurityFilterChain that specifies the security configuration.
     * @throws Exception - If any error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection for stateless apps
                .cors(withDefaults())           // Aktivera CORS med standardinställningar
                .authorizeHttpRequests(auth -> {
                    // Tillåt alla att komma åt /auth/**-vägar utan autentisering
                    auth.requestMatchers("/auth/**").permitAll();
                    // Tillåt alla att komma åt Swagger UI
                    auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll();
                    // Tillåt användare med roller USER eller ADMIN att komma åt /user/**
                    auth.requestMatchers("/api/**").hasAnyRole("USER", "ADMIN");
                    // Tillåt endast användare med rollen ADMIN att komma åt /admin/**
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                    // Kräver autentisering för alla övriga begäranden
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Configures CORS filter to allow specific domains, methods, and headers.
     * This enables clients from other domains to send HTTP requests to the server.
     *
     * @return CorsFilter that handles CORS configuration for all incoming requests.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow only requests from specific localhost and frontend ports
        config.setAllowedOrigins(List.of("http://localhost:8080, http://localhost:3000"));
        // Allow specific HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        // Allow headers such as Authorization and Content-Type
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        // Create a source that matches all URL patterns (/**) with the above configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

