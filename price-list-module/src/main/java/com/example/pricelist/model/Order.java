package com.example.pricelist.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model class representing a customer order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private List<OrderItem> items = new ArrayList<>();
    private double total;
    private String customerName;
    private String customerContact;
    private String paymentMethod;
    private String paymentStatus; // PENDING, COMPLETED, FAILED
    private String orderStatus; // NEW, PROCESSING, COMPLETED, CANCELLED
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;
    
    /**
     * Constructor with basic order information
     */
    public Order(String customerName, String customerContact, String paymentMethod) {
        this.id = UUID.randomUUID().toString();
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING";
        this.orderStatus = "NEW";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    /**
     * Create an order from a cart
     * 
     * @param cart The cart to convert to an order
     * @param customerName The customer name
     * @param customerContact The customer contact information
     * @param paymentMethod The payment method
     * @return A new Order
     */
    public static Order fromCart(Cart cart, String customerName, String customerContact, String paymentMethod) {
        Order order = new Order(customerName, customerContact, paymentMethod);
        
        // Convert cart items to order items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.fromCartItem(cartItem, order.getId());
            order.getItems().add(orderItem);
        }
        
        // Calculate total
        order.setTotal(cart.getTotal());
        
        return order;
    }
    
    /**
     * Calculate the total from all order items
     * 
     * @return The total order amount
     */
    public double calculateTotal() {
        double calculatedTotal = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
        this.total = calculatedTotal;
        return calculatedTotal;
    }
    
    /**
     * Update the order status
     * 
     * @param status The new status
     */
    public void updateStatus(String status) {
        this.orderStatus = status;
        this.updatedAt = new Date();
    }
    
    /**
     * Update the payment status
     * 
     * @param status The new payment status
     */
    public void updatePaymentStatus(String status) {
        this.paymentStatus = status;
        this.updatedAt = new Date();
    }
}
