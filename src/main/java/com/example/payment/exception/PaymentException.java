package com.example.payment.exception;

/**
 * Custom exception for payment-related errors.
 */
public class PaymentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
