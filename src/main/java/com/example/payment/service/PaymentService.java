package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.exception.PaymentException;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for payment business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Create a new payment.
     *
     * @param request payment request details
     * @return created payment
     */
    public Payment createPayment(PaymentRequest request) {
        log.info("Creating payment for merchant: {}", request.getMerchantId());
        
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Payment amount must be greater than zero");
        }
        
        // Create payment entity
        Payment payment = new Payment();
        payment.setMerchantId(request.getMerchantId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setCustomerEmail(request.getCustomerEmail());
        payment.setDescription(request.getDescription());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        return savedPayment;
    }

    /**
     * Get payment by ID.
     *
     * @param id payment ID
     * @return payment details
     */
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    /**
     * Get payment by transaction ID.
     *
     * @param transactionId transaction ID
     * @return payment details
     */
    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("transactionId", transactionId));
    }

    /**
     * Get all payments.
     *
     * @return list of all payments
     */
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll();
    }

    /**
     * Get payments by merchant ID.
     *
     * @param merchantId merchant identifier
     * @return list of payments
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByMerchantId(String merchantId) {
        log.info("Fetching payments for merchant: {}", merchantId);
        return paymentRepository.findByMerchantId(merchantId);
    }

    /**
     * Process payment (simulated).
     *
     * @param id payment ID
     * @return updated payment
     */
    public Payment processPayment(Long id) {
        log.info("Processing payment with ID: {}", id);
        Payment payment = getPaymentById(id);
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentException("Payment cannot be processed in current status: " 
                                     + payment.getStatus());
        }
        
        // Simulate payment processing
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        
        // Simulate successful processing
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        Payment processed = paymentRepository.save(payment);
        
        log.info("Payment processed successfully: {}", id);
        return processed;
    }

    /**
     * Refund payment.
     *
     * @param id payment ID
     * @return refunded payment
     */
    public Payment refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);
        Payment payment = getPaymentById(id);
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new PaymentException("Only completed payments can be refunded");
        }
        
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment refunded = paymentRepository.save(payment);
        
        log.info("Payment refunded successfully: {}", id);
        return refunded;
    }

    /**
     * Cancel payment.
     *
     * @param id payment ID
     * @return cancelled payment
     */
    public Payment cancelPayment(Long id) {
        log.info("Cancelling payment with ID: {}", id);
        Payment payment = getPaymentById(id);
        
        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED 
            || payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
            throw new PaymentException("Cannot cancel completed or refunded payments");
        }
        
        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        Payment cancelled = paymentRepository.save(payment);
        
        log.info("Payment cancelled successfully: {}", id);
        return cancelled;
    }

    /**
     * Update payment status.
     *
     * @param id payment ID
     * @param status new payment status
     * @return updated payment
     */
    public Payment updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        log.info("Updating payment status for ID: {} to {}", id, status);
        Payment payment = getPaymentById(id);
        payment.setStatus(status);
        Payment updated = paymentRepository.save(payment);
        log.info("Payment status updated successfully: {}", id);
        return updated;
    }

    /**
     * Get payment statistics.
     *
     * @return statistics map
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getPaymentStatistics() {
        log.info("Fetching payment statistics");
        long totalPayments = paymentRepository.count();
        long pendingPayments = paymentRepository.countPendingPayments();
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalPayments", totalPayments);
        stats.put("pendingPayments", pendingPayments);
        stats.put("completedPayments", 
                 paymentRepository.findByStatus(Payment.PaymentStatus.COMPLETED).size());
        
        return stats;
    }
}
