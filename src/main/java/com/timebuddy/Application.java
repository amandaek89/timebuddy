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
     * Load environment variables from the .env file using Dotenv library.
     * This will load the variables when the application starts.
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
        // Set the JWT properties as system properties (from the loaded .env file).
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Set the database connection properties as system properties (from the loaded .env file).
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
        // Set up the DataSource for MySQL using DriverManagerDataSource.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // Use the environment variables set as system properties.
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
