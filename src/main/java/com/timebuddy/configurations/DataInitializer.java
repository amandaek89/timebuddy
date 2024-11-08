package com.timebuddy.configurations;


import com.timebuddy.models.Role;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;

    /**
     * Skapar en CommandLineRunner som initialiserar en admin-användare om den inte redan finns i databasen.
     *
     * @return CommandLineRunner som skapar en admin-användare vid programstart om den saknas.
     */
    @Autowired
    DataInitializer(PasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }
    @Bean
    CommandLineRunner initAdminUser() {
        return args -> {
            // Kontrollera om användaren med användarnamnet "admin" redan finns
            if (userRepo.findByUsername("admin") == null) {
                // Skapa en ny admin-användare
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));

                // Sätt användarroller
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ROLE_ADMIN);
                admin.setAuthorities(roles);

                // Sätt skapelse- och uppdateringsdatum
                admin.setCreatedAt(new Date());
                admin.setUpdatedAt(new Date());

                // Spara admin-användaren i databasen
                userRepo.save(admin);
                System.out.println("Admin user created");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }
}
