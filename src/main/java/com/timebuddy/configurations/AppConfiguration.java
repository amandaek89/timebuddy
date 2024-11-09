package com.timebuddy.configurations;

import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class AppConfiguration {

    private final UserRepository userRepo;

    /**
     * Constructor to inject the UserRepository dependency.
     *
     * @param userRepo the repository for managing user data.
     */
    @Autowired
    public AppConfiguration(UserRepository userRepo) {
        this.userRepo = userRepo;
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
