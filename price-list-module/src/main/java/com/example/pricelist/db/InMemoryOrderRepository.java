package com.example.pricelist.db;

import com.example.pricelist.model.Order;
import com.example.pricelist.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of OrderRepository.
 * This is a temporary solution until the Firestore implementation is fixed.
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
    
    @Override
    public Order getOrderById(String id) {
        return orders.get(id);
    }
    
    @Override
    public Order saveOrder(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }
        
        // Ensure order items have IDs and order ID set
        for (OrderItem item : order.getItems()) {
            if (item.getId() == null) {
                item.setId(UUID.randomUUID().toString());
            }
            item.setOrderId(order.getId());
        }
        
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public Order updateOrder(Order order) {
        if (order.getId() == null || !orders.containsKey(order.getId())) {
            throw new IllegalArgumentException("Order not found");
        }
        
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public List<Order> getOrdersByDateRange(Date start, Date end) {
        return orders.values().stream()
                .filter(order -> {
                    Date createdAt = order.getCreatedAt();
                    if (createdAt == null) return false;
                    
                    return !createdAt.before(start) && !createdAt.after(end);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orders.values().stream()
                .filter(order -> status.equals(order.getOrderStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> getOrdersByCustomer(String customerName) {
        return orders.values().stream()
                .filter(order -> customerName.equals(order.getCustomerName()))
                .collect(Collectors.toList());
    }
}
