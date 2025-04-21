package com.example.pricelist.db;

import com.example.pricelist.model.Payment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Payment operations.
 */
@Repository
public interface PaymentRepository {
    /**
     * Get a payment by ID.
     *
     * @param id The payment ID
     * @return The payment, or null if not found
     */
    Payment getPaymentById(String id);
    
    /**
     * Get a payment by order ID.
     *
     * @param orderId The order ID
     * @return The payment, or null if not found
     */
    Payment getPaymentByOrderId(String orderId);
    
    /**
     * Save a payment.
     *
     * @param payment The payment to save
     * @return The saved payment
     */
    Payment savePayment(Payment payment);
    
    /**
     * Update a payment.
     *
     * @param payment The payment to update
     * @return The updated payment
     */
    Payment updatePayment(Payment payment);
    
    /**
     * Get payments by status.
     *
     * @param status The payment status
     * @return List of payments with the specified status
     */
    List<Payment> getPaymentsByStatus(String status);
    
    /**
     * Get payments by date range.
     *
     * @param start The start date
     * @param end The end date
     * @return List of payments in the date range
     */
    List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Get payments by payment method.
     *
     * @param method The payment method
     * @return List of payments with the specified method
     */
    List<Payment> getPaymentsByMethod(String method);
}
