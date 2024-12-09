package com.timebuddy.configurations;

import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import com.timebuddy.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

/**
 * Configuration class for application security and user management.
 * Handles password encryption and user authentication details.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.timebuddy.repositories")  // Se till att paketet med repositories är korrekt
@ComponentScan(basePackages = "com.timebuddy")
public class AppConfiguration {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    /**
     * Constructor to inject the UserRepository dependency.
     *
     * @param userRepo the repository for managing user data.
     */
    @Autowired
    public AppConfiguration(UserRepository userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    /**
        * Bean for creating a JwtAuthenticationFilter instance.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        return new JwtAuthenticationFilter(userDetailsService, jwtService);
    }

    /**
     * Bean for password encryption using BCrypt.
     *
     * @return an instance of BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean for loading user details based on username.
     * Fetches user information from the database.
     *
     * @return a UserDetailsService that retrieves user details from the database.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Attempt to find the user in the database
            Optional<User> user = userRepo.findByUsername(username);

            // If the user is found, return it as a UserDetails instance
            return user
                    .map(u -> (UserDetails) u)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username '" + username + "' not found."));
        };
    }
}
