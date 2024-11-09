package com.timebuddy.configurations;

import com.timebuddy.models.Role;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A class responsible for initializing default data in the application database.
 * This includes creating an admin user if it does not already exist.
 */
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    /**
     * Constructor for dependency injection.
     *
     * @param passwordEncoder the password encoder used to securely hash passwords
     * @param userRepo        the repository for accessing and manipulating user data
     */
    @Autowired
    public DataInitializer(PasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    /**
     * Initializes the application with default data upon startup.
     * Creates an admin user with the username "admin" if it does not already exist.
     *
     * @return a CommandLineRunner that executes the initialization logic
     */
    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            logger.info("Checking if admin user exists...");

            // Check if a user with the username "admin" already exists
            if (!userRepo.findByUsername("admin").isPresent()) {
                logger.info("Admin user not found. Creating admin user...");

                // Create a new admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));

                // Set roles for the admin user
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ROLE_ADMIN);
                admin.setAuthorities(roles);

                // Set creation and update timestamps
                admin.setCreatedAt(new Date());
                admin.setUpdatedAt(new Date());

                // Save the admin user in the database
                userRepo.save(admin);

                logger.info("Admin user created successfully.");
            } else {
                logger.info("Admin user already exists. No changes made.");
            }
        };
    }
}
