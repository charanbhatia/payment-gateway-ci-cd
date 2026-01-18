package com.example.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Payment Gateway Microservice.
 * Handles payment processing, validation, and transaction management.
 *
 * @author DevOps CI/CD Project
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class PaymentApplication {

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
