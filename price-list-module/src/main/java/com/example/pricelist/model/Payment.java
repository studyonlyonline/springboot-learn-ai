package com.example.pricelist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Model class representing a payment for an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private String orderId;
    private double amount;
    private String method; // CASH, CREDIT_CARD, DEBIT_CARD, UPI, etc.
    private String status; // PENDING, COMPLETED, FAILED
    private String transactionId;
    private Date timestamp;
    
    /**
     * Constructor with basic payment information
     */
    public Payment(String orderId, double amount, String method) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = "PENDING";
        this.timestamp = new Date();
    }
    
    /**
     * Create a payment for an order
     * 
     * @param order The order
     * @return A new Payment
     */
    public static Payment forOrder(Order order) {
        return new Payment(order.getId(), order.getTotal(), order.getPaymentMethod());
    }
    
    /**
     * Complete the payment
     * 
     * @param transactionId The transaction ID (optional)
     */
    public void complete(String transactionId) {
        this.status = "COMPLETED";
        this.transactionId = transactionId;
        this.timestamp = new Date();
    }
    
    /**
     * Mark the payment as failed
     * 
     * @param reason The failure reason (optional)
     */
    public void fail(String reason) {
        this.status = "FAILED";
        this.transactionId = reason;
        this.timestamp = new Date();
    }
}
