package com.timebuddy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Main application class for starting the Spring Boot application.
 * This class loads environment variables from a .env file,
 * sets up the database connection properties, and runs the Spring Boot application.
 *
 * The application is configured to connect to a MySQL database using the provided
 * environment variables for the database URL, username, and password.
 */
@SpringBootApplication
public class Application {

    /**
     * Load environment variables from the .env file using Dotenv library.
     */
    private static final Dotenv dotenv = Dotenv.load();
    /**
     * Main method to launch the Spring Boot application.
     * It loads the environment variables from the .env file and sets system properties
     * for the database connection, then runs the Spring application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Load environment variables from the .env file using Dotenv library.
        Dotenv dotenv = Dotenv.load();

        // Set the JWT properties as system properties.
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Set the database connection properties as system properties.
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        // Run the Spring Boot application.
        SpringApplication.run(Application.class, args);
    }

    /**
     * Bean to configure the DataSource for connecting to the MySQL database.
     * Uses the environment variables loaded from the .env file for database configuration.
     *
     * @return DataSource configured with database connection details.
     */
    @Bean
    public DataSource dataSource() {
        // Load environment variables from the .env file.
        Dotenv dotenv = Dotenv.load();

        // Set up the DataSource for MySQL using DriverManagerDataSource.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));

        // Return the configured DataSource.
        return dataSource;
    }
}
