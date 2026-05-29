package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(EcommerceApplication.class, args);
    }

    // Reads KEY=VALUE pairs from .env file and sets them as system properties
    // so Spring can resolve ${KEY} placeholders in application.properties
    private static void loadDotEnv() {
        var envPath = Paths.get(".env");
        if (!Files.exists(envPath)) return;
        try (BufferedReader reader = Files.newBufferedReader(envPath)) {
            reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#") && line.contains("="))
                .forEach(line -> {
                    int idx = line.indexOf('=');
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (!key.isEmpty()) System.setProperty(key, value);
                });
            System.out.println(".env loaded successfully");
        } catch (IOException e) {
            System.err.println("Warning: Could not load .env — " + e.getMessage());
        }
    }
}
