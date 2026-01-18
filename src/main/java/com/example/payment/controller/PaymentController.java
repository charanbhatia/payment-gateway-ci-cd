package com.example.payment.controller;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment API", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Health check endpoint.
     */
    @GetMapping("/ping")
    @Operation(summary = "Health check", description = "Check if API is alive")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Payment Gateway is running",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Create a new payment.
     */
    @PostMapping
    @Operation(summary = "Create payment", description = "Create a new payment transaction")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {
        log.info("Received payment creation request from merchant: {}", 
                request.getMerchantId());
        Payment payment = paymentService.createPayment(request);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get payment by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get payment", description = "Get payment details by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentService.getPaymentById(id);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment retrieved successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by transaction ID.
     */
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", 
               description = "Get payment details by transaction ID")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        Payment payment = paymentService.getPaymentByTransactionId(transactionId);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment retrieved successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments.
     */
    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve all payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponse> responses = payments.stream()
                .map(p -> PaymentResponse.fromEntity(p, "Success"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get payments by merchant ID.
     */
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get payments by merchant", 
               description = "Get all payments for a specific merchant")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMerchantId(
            @PathVariable String merchantId) {
        log.info("Fetching payments for merchant: {}", merchantId);
        List<Payment> payments = paymentService.getPaymentsByMerchantId(merchantId);
        List<PaymentResponse> responses = payments.stream()
                .map(p -> PaymentResponse.fromEntity(p, "Success"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Process payment.
     */
    @PostMapping("/{id}/process")
    @Operation(summary = "Process payment", 
               description = "Process a pending payment")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long id) {
        log.info("Processing payment with ID: {}", id);
        Payment payment = paymentService.processPayment(id);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment processed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Refund payment.
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", 
               description = "Refund a completed payment")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        log.info("Refunding payment with ID: {}", id);
        Payment payment = paymentService.refundPayment(id);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment refunded successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel payment.
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel payment", 
               description = "Cancel a pending payment")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id) {
        log.info("Cancelling payment with ID: {}", id);
        Payment payment = paymentService.cancelPayment(id);
        PaymentResponse response = PaymentResponse.fromEntity(payment, 
                                    "Payment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment statistics.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", 
               description = "Get payment processing statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Fetching payment statistics");
        Map<String, Object> stats = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(stats);
    }
}
