package com.timebuddy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Ladda miljövariabler från .env-filen till systemets miljövariabler
        Dotenv dotenv = Dotenv.configure()
                .load();

        // Sätt varje miljövariabel som systemegenskap så att Spring Boot kan använda dem
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // Kör Spring Boot-applikationen
        SpringApplication.run(Application.class, args);
    }
}
