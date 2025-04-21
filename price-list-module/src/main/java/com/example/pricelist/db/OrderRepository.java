package com.example.pricelist.db;

import com.example.pricelist.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Order operations.
 */
@Repository
public interface OrderRepository {
    /**
     * Get all orders.
     *
     * @return List of all orders
     */
    List<Order> getAllOrders();
    
    /**
     * Get an order by ID.
     *
     * @param id The order ID
     * @return The order, or null if not found
     */
    Order getOrderById(String id);
    
    /**
     * Save an order.
     *
     * @param order The order to save
     * @return The saved order
     */
    Order saveOrder(Order order);
    
    /**
     * Update an order.
     *
     * @param order The order to update
     * @return The updated order
     */
    Order updateOrder(Order order);
    
    /**
     * Get orders by date range.
     *
     * @param start The start date
     * @param end The end date
     * @return List of orders in the date range
     */
    List<Order> getOrdersByDateRange(Date start, Date end);
    
    /**
     * Get orders by status.
     *
     * @param status The order status
     * @return List of orders with the specified status
     */
    List<Order> getOrdersByStatus(String status);
    
    /**
     * Get orders by customer.
     *
     * @param customerName The customer name
     * @return List of orders for the customer
     */
    List<Order> getOrdersByCustomer(String customerName);
}
