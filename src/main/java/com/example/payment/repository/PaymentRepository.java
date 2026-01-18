package com.example.payment.repository;

import com.example.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by transaction ID.
     *
     * @param transactionId unique transaction identifier
     * @return Optional containing payment if found
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find all payments by merchant ID.
     *
     * @param merchantId merchant identifier
     * @return List of payments for the merchant
     */
    List<Payment> findByMerchantId(String merchantId);

    /**
     * Find payments by status.
     *
     * @param status payment status
     * @return List of payments with specified status
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /**
     * Find all payments by customer email.
     *
     * @param email customer email address
     * @return List of payments for the customer
     */
    List<Payment> findByCustomerEmail(String email);

    /**
     * Count pending payments.
     *
     * @return count of pending payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    long countPendingPayments();
}
