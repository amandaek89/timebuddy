package com.timebuddy.configurations;

import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfiguration {

    private final UserRepository userRepo;

    /**
     * Konstruktor för AppConfig som injicerar UserRepo.
     *
     * @param userRepo Användarrepository som används för att hantera användardata.
     */
    @Autowired
    public AppConfiguration(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    /**
     * Skapar en BCryptPasswordEncoder för att kryptera användarlösenord.
     *
     * @return en instans av BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Skapar en PasswordEncoder för att kryptera lösenord
    }

    /**
     * Skapar en UserDetailsService som används för att ladda användardetaljer baserat på användarnamn.
     * Hämtar användare från UserRepo.
     *
     * @return en UserDetailsService som hämtar användarinformation från UserRepo.
     * @throws UsernameNotFoundException om användaren inte finns.
     */
    @Bean
    UserDetailsService userDetailsService() {
        try {
            return username -> (UserDetails) userRepo.findByUsername(username); // Hämtar användardetaljer från userRepo
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found"); // Kastar undantag om användaren inte hittas
        }
    }
}
