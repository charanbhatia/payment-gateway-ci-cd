package com.example.payment.controller;

import com.example.payment.model.Payment;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for PaymentController.
 */
@WebMvcTest(controllers = PaymentController.class)
@Import({PaymentController.class})
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class
})
@MockBean(JpaMetamodelMappingContext.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private Payment testPayment;
    private PaymentRequest testRequest;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setMerchantId("MERCHANT_123");
        testPayment.setAmount(new BigDecimal("100.00"));
        testPayment.setCurrency("USD");
        testPayment.setStatus(Payment.PaymentStatus.PENDING);
        testPayment.setTransactionId("TXN-12345");

        testRequest = new PaymentRequest();
        testRequest.setMerchantId("MERCHANT_123");
        testRequest.setAmount(new BigDecimal("100.00"));
        testRequest.setCurrency("USD");
        testRequest.setPaymentMethod("CARD");
        testRequest.setDescription("Test payment");
        testRequest.setCustomerEmail("test@example.com");
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/payments/ping"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.message").value("Payment Gateway is running"));
    }

    @Test
    void testPing() throws Exception {
        mockMvc.perform(get("/api/v1/payments/ping"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Payment Gateway is running"))
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testCreatePayment_Success() throws Exception {
        // Arrange
        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(testPayment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.merchantId").value("MERCHANT_123"))
            .andExpect(jsonPath("$.amount").value(100.00))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreatePayment_ValidationError_NullAmount() throws Exception {
        // Arrange
        testRequest.setAmount(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePayment_ValidationError_InvalidCurrency() throws Exception {
        // Arrange
        testRequest.setCurrency("US");

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPayment_Success() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(1L)).thenReturn(testPayment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.merchantId").value("MERCHANT_123"));
    }

    @Test
    void testGetAllPayments() throws Exception {
        // Arrange
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testRefundPayment_Success() throws Exception {
        // Arrange
        testPayment.setStatus(Payment.PaymentStatus.REFUNDED);
        when(paymentService.refundPayment(1L)).thenReturn(testPayment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments/1/refund"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REFUNDED"));
    }

    // Removed: testUpdatePaymentStatus_Success since controller does not expose this endpoint.
}
