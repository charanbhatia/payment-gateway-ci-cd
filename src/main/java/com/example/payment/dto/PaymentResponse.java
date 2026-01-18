package com.example.payment.dto;

import com.example.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for payment responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String transactionId;
    private String merchantId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String customerEmail;
    private Payment.PaymentStatus status;
    private String description;
    private LocalDateTime createdAt;
    private String message;

    /**
     * Create response from Payment entity.
     */
    public static PaymentResponse fromEntity(Payment payment, String message) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setMerchantId(payment.getMerchantId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setCustomerEmail(payment.getCustomerEmail());
        response.setStatus(payment.getStatus());
        response.setDescription(payment.getDescription());
        response.setCreatedAt(payment.getCreatedAt());
        response.setMessage(message);
        return response;
    }
}
