package com.timebuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    /**
     * Main method to launch the Spring Boot application.
     * It runs the Spring application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Run the Spring Boot application.
        SpringApplication.run(Application.class, args);
    }

    /**
     * Bean to configure the DataSource for connecting to the MySQL database.
     * Uses values injected via Spring's @Value annotation.
     *
     * @return DataSource configured with database connection details.
     */
    @Bean
    public DataSource dataSource(
            @Value("${DB_URL}") String dbUrl,
            @Value("${DB_USERNAME}") String dbUsername,
            @Value("${DB_PASSWORD}") String dbPassword) {

        // Ensure all required environment variables are present.
        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new IllegalStateException("Database connection properties are not properly set.");
        }

        // Set up the DataSource for MySQL using DriverManagerDataSource.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        // Return the configured DataSource.
        return dataSource;
    }
}
