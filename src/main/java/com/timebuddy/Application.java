package com.timebuddy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import com.mysql.cj.jdbc.Driver;
import io.github.cdimascio.dotenv.Dotenv;
import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    /**
     * Main method to launch the Spring Boot application.
     * It loads the environment variables from the system environment and sets system properties
     * for the database connection, then runs the Spring application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Get environment variables directly from the system environment (no .env file needed).
        String jwtExpiration = System.getenv("JWT_EXPIRATION");
        String jwtSecret = System.getenv("JWT_SECRET");
        String dbUrl = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        // Ensure all required environment variables are present.
        if (jwtExpiration == null || jwtSecret == null || dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new IllegalStateException("Required environment variables are missing.");
        }

        // Set the JWT properties as system properties (useful for Spring Security or other components).
        System.setProperty("JWT_EXPIRATION", jwtExpiration);
        System.setProperty("JWT_SECRET", jwtSecret);

        // Set the database connection properties as system properties.
        System.setProperty("DB_URL", dbUrl);
        System.setProperty("DB_USERNAME", dbUsername);
        System.setProperty("DB_PASSWORD", dbPassword);

        // Run the Spring Boot application.
        SpringApplication.run(Application.class, args);
    }

    /**
     * Bean to configure the DataSource for connecting to the MySQL database.
     * Uses the environment variables set in the system environment for database configuration.
     *
     * @return DataSource configured with database connection details.
     */
    @Bean
    public DataSource dataSource() {
        // Set up the DataSource for MySQL using DriverManagerDataSource.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // Use the environment variables set in the system environment.
        String dbUrl = System.getProperty("DB_URL");
        String dbUsername = System.getProperty("DB_USERNAME");
        String dbPassword = System.getProperty("DB_PASSWORD");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new IllegalStateException("Database connection properties are not properly set.");
        }

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        // Return the configured DataSource.
        return dataSource;
    }
}
