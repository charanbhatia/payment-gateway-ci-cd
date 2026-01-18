package com.example.payment.exception;

/**
 * Exception thrown when payment is not found.
 */
public class PaymentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(Long id) {
        super("Payment not found with ID: " + id);
    }

    public PaymentNotFoundException(String field, String value) {
        super("Payment not found with " + field + ": " + value);
    }
}
