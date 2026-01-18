package com.example.payment.service;

import com.example.payment.exception.InvalidPaymentException;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.model.Payment;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PaymentService.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest validPaymentRequest;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        validPaymentRequest = new PaymentRequest();
        validPaymentRequest.setMerchantId("MERCHANT_123");
        validPaymentRequest.setAmount(new BigDecimal("100.00"));
        validPaymentRequest.setCurrency("USD");
        validPaymentRequest.setDescription("Test payment");
        validPaymentRequest.setCustomerEmail("test@example.com");

        savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setMerchantId("MERCHANT_123");
        savedPayment.setAmount(new BigDecimal("100.00"));
        savedPayment.setCurrency("USD");
        savedPayment.setStatus(Payment.PaymentStatus.PENDING);
        savedPayment.setTransactionId("TXN-12345");
    }

    @Test
    void testCreatePayment_Success() {
        // Arrange
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.createPayment(validPaymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals("MERCHANT_123", result.getMerchantId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(Payment.PaymentStatus.PENDING, result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_InvalidAmount_Zero() {
        // Arrange
        validPaymentRequest.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.createPayment(validPaymentRequest);
        });
    }

    @Test
    void testCreatePayment_InvalidAmount_Negative() {
        // Arrange
        validPaymentRequest.setAmount(new BigDecimal("-10.00"));

        // Act & Assert
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.createPayment(validPaymentRequest);
        });
    }

    @Test
    void testCreatePayment_AmountExceedsLimit() {
        // Arrange
        validPaymentRequest.setAmount(new BigDecimal("15000.00"));

        // Act & Assert
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.createPayment(validPaymentRequest);
        });
    }

    @Test
    void testCreatePayment_InvalidCurrency() {
        // Arrange
        validPaymentRequest.setCurrency("XYZ");

        // Act & Assert
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.createPayment(validPaymentRequest);
        });
    }

    @Test
    void testGetPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));

        // Act
        Payment result = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Arrange
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(999L);
        });
    }

    @Test
    void testGetAllPayments() {
        // Arrange
        List<Payment> payments = Arrays.asList(savedPayment);
        when(paymentRepository.findAll()).thenReturn(payments);

        // Act
        List<Payment> result = paymentService.getAllPayments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testGetPaymentsByMerchant() {
        // Arrange
        List<Payment> payments = Arrays.asList(savedPayment);
        when(paymentRepository.findByMerchantId("MERCHANT_123")).thenReturn(payments);

        // Act
        List<Payment> result = paymentService.getPaymentsByMerchantId("MERCHANT_123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MERCHANT_123", result.get(0).getMerchantId());
        verify(paymentRepository, times(1)).findByMerchantId("MERCHANT_123");
    }

    @Test
    void testRefundPayment_Success() {
        // Arrange
        savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.refundPayment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Payment.PaymentStatus.REFUNDED, result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testRefundPayment_InvalidStatus() {
        // Arrange
        savedPayment.setStatus(Payment.PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));

        // Act & Assert
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.refundPayment(1L);
        });
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.updatePaymentStatus(1L, Payment.PaymentStatus.COMPLETED);

        // Assert
        assertNotNull(result);
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
