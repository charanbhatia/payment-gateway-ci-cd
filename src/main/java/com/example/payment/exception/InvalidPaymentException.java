package com.example.payment.exception;

/**
 * Exception thrown when payment validation fails.
 */
public class InvalidPaymentException extends RuntimeException {

    /**
     * Constructs a new InvalidPaymentException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidPaymentException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidPaymentException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
