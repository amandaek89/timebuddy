package com.timebuddy;

import io.github.cdimascio.dotenv.Dotenv;

public class Application {
    public static void main(String[] args) {
        // Ladda .env-filen
        Dotenv dotenv = Dotenv.load();

        // Hämta databasinställningar
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        // Resten av applikationen...
    }
}

