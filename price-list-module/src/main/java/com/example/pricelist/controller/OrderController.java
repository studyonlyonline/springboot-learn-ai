package com.example.pricelist.controller;

import com.example.pricelist.model.Order;
import com.example.pricelist.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for order operations.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Display the orders page.
     *
     * @return ModelAndView containing the view name and model attributes
     */
    @GetMapping
    public ModelAndView getOrdersPage() {
        ModelAndView modelAndView = new ModelAndView("price-list/orders");
        List<Order> orders = orderService.getAllOrders();
        modelAndView.addObject("orders", orders);
        return modelAndView;
    }
    
    /**
     * Display the order details page.
     *
     * @param id The order ID
     * @return ModelAndView containing the view name and model attributes
     */
    @GetMapping("/{id}")
    public ModelAndView getOrderDetailsPage(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return new ModelAndView("redirect:/orders");
        }
        
        ModelAndView modelAndView = new ModelAndView("price-list/order-details");
        modelAndView.addObject("order", order);
        
        // Calculate total quantity to fix the Thymeleaf error
        int totalQuantity = order.getItems().stream()
                                .mapToInt(item -> item.getQuantity())
                                .sum();
        modelAndView.addObject("totalQuantity", totalQuantity);
        
        return modelAndView;
    }
    
    /**
     * Get all orders.
     *
     * @return List of all orders
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get an order by ID.
     *
     * @param id The order ID
     * @return The order
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Order not found: " + id);
            return ResponseEntity.badRequest().body(error);
        }
        
        return ResponseEntity.ok(order);
    }
    
    /**
     * Update an order's status.
     *
     * @param id The order ID
     * @param status The new status
     * @return The updated order
     */
    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get orders by date range.
     *
     * @param start The start date
     * @param end The end date
     * @return List of orders in the date range
     */
    @GetMapping("/api/by-date")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
        List<Order> orders = orderService.getOrdersByDateRange(start, end);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get orders by status.
     *
     * @param status The order status
     * @return List of orders with the specified status
     */
    @GetMapping("/api/by-status")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get orders by customer.
     *
     * @param customerName The customer name
     * @return List of orders for the customer
     */
    @GetMapping("/api/by-customer")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByCustomer(@RequestParam String customerName) {
        List<Order> orders = orderService.getOrdersByCustomer(customerName);
        return ResponseEntity.ok(orders);
    }
}
