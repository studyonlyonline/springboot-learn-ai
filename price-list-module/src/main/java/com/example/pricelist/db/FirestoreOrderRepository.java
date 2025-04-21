package com.example.pricelist.db;

import com.example.common.service.FirestoreInitializer;
import com.example.pricelist.model.Order;
import com.example.pricelist.model.OrderItem;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Firestore implementation of OrderRepository.
 */
@Repository
public class FirestoreOrderRepository implements OrderRepository {
    private final Firestore db;
    private final Cache<String, Order> orderCache;
    private final Cache<String, List<Order>> ordersListCache;
    
    private static final String ORDERS_COLLECTION = "priceList_showroom_orders_collection";
    private static final String ORDER_ITEMS_COLLECTION = "priceList_showroom_order_items_collection";
    
    @Autowired
    public FirestoreOrderRepository(FirestoreInitializer firestoreInitializer) {
        this.db = firestoreInitializer.getDb();
        this.orderCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        this.ordersListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(20)
                .build();
    }
    
    @Override
    public List<Order> getAllOrders() {
        return ordersListCache.get("allOrders", key -> {
            try {
                List<Order> orders = new ArrayList<>();
                
                // Get all order documents
                ApiFuture<QuerySnapshot> future = db.collection(ORDERS_COLLECTION).get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                
                for (QueryDocumentSnapshot document : documents) {
                    Order order = documentToOrder(document);
                    
                    // Load order items
                    List<OrderItem> items = fetchOrderItems(order.getId());
                    order.setItems(items);
                    
                    orders.add(order);
                    
                    // Update individual order cache
                    orderCache.put(order.getId(), order);
                }
                
                return orders;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public Order getOrderById(String id) {
        return orderCache.get(id, key -> {
            try {
                // Get order document
                DocumentSnapshot orderDoc = db.collection(ORDERS_COLLECTION).document(id).get().get();
                if (!orderDoc.exists()) {
                    return null;
                }
                
                Order order = documentToOrder(orderDoc);
                
                // Load order items
                List<OrderItem> items = fetchOrderItems(id);
                order.setItems(items);
                
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    
    @Override
    public Order saveOrder(Order order) {
        try {
            // If no ID is provided, generate one
            if (order.getId() == null) {
                order.setId(UUID.randomUUID().toString());
            }
            
            // Save order document
            Map<String, Object> orderData = orderToMap(order);
            db.collection(ORDERS_COLLECTION).document(order.getId()).set(orderData).get();
            
            // Save order items
            for (OrderItem item : order.getItems()) {
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID().toString());
                }
                item.setOrderId(order.getId());
                saveOrderItem(item);
            }
            
            // Update cache
            orderCache.put(order.getId(), order);
            
            // Invalidate orders list cache
            ordersListCache.invalidate("allOrders");
            
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Order updateOrder(Order order) {
        try {
            // Save order document
            Map<String, Object> orderData = orderToMap(order);
            db.collection(ORDERS_COLLECTION).document(order.getId()).set(orderData).get();
            
            // Update cache
            orderCache.put(order.getId(), order);
            
            // Invalidate orders list cache
            ordersListCache.invalidate("allOrders");
            
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Order> getOrdersByDateRange(Date start, Date end) {
        String cacheKey = "dateRange_" + start.getTime() + "_" + end.getTime();
        
        return ordersListCache.get(cacheKey, key -> {
            try {
                // Get all orders and filter by date range
                List<Order> allOrders = getAllOrders();
                
                return allOrders.stream()
                        .filter(order -> {
                            Date createdAt = order.getCreatedAt();
                            if (createdAt == null) return false;
                            
                            return !createdAt.before(start) && !createdAt.after(end);
                        })
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public List<Order> getOrdersByStatus(String status) {
        String cacheKey = "status_" + status;
        
        return ordersListCache.get(cacheKey, key -> {
            try {
                // Query orders by status
                ApiFuture<QuerySnapshot> future = db.collection(ORDERS_COLLECTION)
                        .whereEqualTo("orderStatus", status)
                        .get();
                
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                List<Order> orders = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : documents) {
                    Order order = documentToOrder(document);
                    
                    // Load order items
                    List<OrderItem> items = fetchOrderItems(order.getId());
                    order.setItems(items);
                    
                    orders.add(order);
                    
                    // Update individual order cache
                    orderCache.put(order.getId(), order);
                }
                
                return orders;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public List<Order> getOrdersByCustomer(String customerName) {
        String cacheKey = "customer_" + customerName;
        
        return ordersListCache.get(cacheKey, key -> {
            try {
                // Query orders by customer name
                ApiFuture<QuerySnapshot> future = db.collection(ORDERS_COLLECTION)
                        .whereEqualTo("customerName", customerName)
                        .get();
                
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                List<Order> orders = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : documents) {
                    Order order = documentToOrder(document);
                    
                    // Load order items
                    List<OrderItem> items = fetchOrderItems(order.getId());
                    order.setItems(items);
                    
                    orders.add(order);
                    
                    // Update individual order cache
                    orderCache.put(order.getId(), order);
                }
                
                return orders;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    /**
     * Convert a DocumentSnapshot to an Order object.
     *
     * @param document The document snapshot
     * @return The Order object
     */
    private Order documentToOrder(DocumentSnapshot document) {
        Order order = new Order();
        
        order.setId(document.getId());
        order.setCustomerName(document.getString("customerName"));
        order.setCustomerContact(document.getString("customerContact"));
        order.setPaymentMethod(document.getString("paymentMethod"));
        order.setPaymentStatus(document.getString("paymentStatus"));
        order.setOrderStatus(document.getString("orderStatus"));
        
        // Handle numeric fields
        Double total = document.getDouble("total");
        if (total != null) {
            order.setTotal(total);
        }
        
        // Handle date fields
        Date createdAt = document.getDate("createdAt");
        if (createdAt != null) {
            order.setCreatedAt(createdAt);
        } else {
            order.setCreatedAt(new Date());
        }
        
        Date updatedAt = document.getDate("updatedAt");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt);
        } else {
            order.setUpdatedAt(new Date());
        }
        
        return order;
    }
    
    /**
     * Convert an Order object to a Map for Firestore.
     *
     * @param order The Order object
     * @return The Map representation
     */
    private Map<String, Object> orderToMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", order.getId());
        map.put("customerName", order.getCustomerName());
        map.put("customerContact", order.getCustomerContact());
        map.put("paymentMethod", order.getPaymentMethod());
        map.put("paymentStatus", order.getPaymentStatus());
        map.put("orderStatus", order.getOrderStatus());
        map.put("total", order.getTotal());
        
        // Handle date fields
        if (order.getCreatedAt() != null) {
            map.put("createdAt", order.getCreatedAt());
        } else {
            map.put("createdAt", new Date());
        }
        
        if (order.getUpdatedAt() != null) {
            map.put("updatedAt", order.getUpdatedAt());
        } else {
            map.put("updatedAt", new Date());
        }
        
        return map;
    }
    
    /**
     * Save an order item to Firestore.
     *
     * @param item The order item to save
     * @throws ExecutionException If an error occurs during execution
     * @throws InterruptedException If the operation is interrupted
     */
    private void saveOrderItem(OrderItem item) throws ExecutionException, InterruptedException {
        db.collection(ORDER_ITEMS_COLLECTION).document(item.getId()).set(item).get();
    }
    
    /**
     * Fetch all items for an order.
     *
     * @param orderId The order ID
     * @return List of order items
     * @throws ExecutionException If an error occurs during execution
     * @throws InterruptedException If the operation is interrupted
     */
    private List<OrderItem> fetchOrderItems(String orderId) throws ExecutionException, InterruptedException {
        List<OrderItem> items = new ArrayList<>();
        
        // Query order items by order ID
        ApiFuture<QuerySnapshot> future = db.collection(ORDER_ITEMS_COLLECTION)
                .whereEqualTo("orderId", orderId)
                .get();
        
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            OrderItem item = document.toObject(OrderItem.class);
            items.add(item);
        }
        
        return items;
    }
}
