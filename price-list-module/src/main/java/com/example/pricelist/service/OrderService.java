package com.example.pricelist.service;

import com.example.pricelist.db.CartRepository;
import com.example.pricelist.db.OrderRepository;
import com.example.pricelist.db.PaymentRepository;
import com.example.pricelist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service for managing orders.
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final ProductDataSource productDataSource;
    
    @Autowired
    public OrderService(
            @Qualifier("firestoreOrderRepository") OrderRepository orderRepository,
            @Qualifier("firestoreCartRepository") CartRepository cartRepository,
            @Qualifier("firestorePaymentRepository") PaymentRepository paymentRepository,
            @Qualifier("firestoreProductDataSource") ProductDataSource productDataSource) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
        this.productDataSource = productDataSource;
    }
    
    /**
     * Create an order from a cart.
     *
     * @param cartId The cart ID
     * @param customerName The customer name
     * @param customerContact The customer contact information
     * @param paymentMethod The payment method
     * @return The created order
     */
    public Order createOrderFromCart(String cartId, String customerName, String customerContact, String paymentMethod) {
        // Get cart
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found: " + cartId);
        }
        
        // Check if cart is empty
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }
        
        // Check stock availability
        for (CartItem item : cart.getItems()) {
            Product product = productDataSource.getProductById(item.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + item.getProductId());
            }
            
            if (item.getQuantity() > product.getStockAvailability()) {
                throw new IllegalArgumentException("Not enough stock available for " + product.getName() + 
                        ". Only " + product.getStockAvailability() + " units available.");
            }
        }
        
        // Create order
        Order order = Order.fromCart(cart, customerName, customerContact, paymentMethod);
        
        // Save order
        order = orderRepository.saveOrder(order);
        
        // Create payment
        Payment payment = Payment.forOrder(order);
        paymentRepository.savePayment(payment);
        
        // Update inventory
        updateInventory(order);
        
        // Clear cart
        cartRepository.deleteCart(cartId);
        
        return order;
    }
    
    /**
     * Get all orders.
     *
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
    
    /**
     * Get an order by ID.
     *
     * @param id The order ID
     * @return The order, or null if not found
     */
    public Order getOrderById(String id) {
        return orderRepository.getOrderById(id);
    }
    
    /**
     * Update an order's status.
     *
     * @param id The order ID
     * @param status The new status
     * @return The updated order
     */
    public Order updateOrderStatus(String id, String status) {
        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        
        order.updateStatus(status);
        return orderRepository.updateOrder(order);
    }
    
    /**
     * Update a payment's status.
     *
     * @param orderId The order ID
     * @param status The new payment status
     * @param transactionId The transaction ID (optional)
     * @return The updated order
     */
    public Order updatePaymentStatus(String orderId, String status, String transactionId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        
        // Update order payment status
        order.updatePaymentStatus(status);
        order = orderRepository.updateOrder(order);
        
        // Update payment
        Payment payment = paymentRepository.getPaymentByOrderId(orderId);
        if (payment != null) {
            if ("COMPLETED".equals(status)) {
                payment.complete(transactionId);
            } else if ("FAILED".equals(status)) {
                payment.fail(transactionId);
            } else {
                payment.setStatus(status);
                payment.setTransactionId(transactionId);
                payment.setTimestamp(new Date());
            }
            paymentRepository.updatePayment(payment);
        }
        
        return order;
    }
    
    /**
     * Get orders by date range.
     *
     * @param start The start date
     * @param end The end date
     * @return List of orders in the date range
     */
    public List<Order> getOrdersByDateRange(Date start, Date end) {
        return orderRepository.getOrdersByDateRange(start, end);
    }
    
    /**
     * Get orders by status.
     *
     * @param status The order status
     * @return List of orders with the specified status
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }
    
    /**
     * Get orders by customer.
     *
     * @param customerName The customer name
     * @return List of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String customerName) {
        return orderRepository.getOrdersByCustomer(customerName);
    }
    
    /**
     * Update inventory after an order is placed.
     *
     * @param order The order
     */
    private void updateInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productDataSource.getProductById(item.getProductId());
            if (product != null) {
                // Update stock
                int newStock = product.getStockAvailability() - item.getQuantity();
                if (newStock < 0) newStock = 0;
                product.setStockAvailability(newStock);
                
                // Save product
                productDataSource.updateProduct(product);
            }
        }
    }
}
