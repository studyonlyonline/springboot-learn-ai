package com.example.pricelist.db;

import com.example.common.service.FirestoreInitializer;
import com.example.pricelist.model.Payment;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Firestore implementation of PaymentRepository.
 */
@Repository
public class FirestorePaymentRepository implements PaymentRepository {
    private final Firestore db;
    private final Cache<String, Payment> paymentCache;
    
    private static final String PAYMENTS_COLLECTION = "priceList_showroom_payments_collection";
    
    @Autowired
    public FirestorePaymentRepository(FirestoreInitializer firestoreInitializer) {
        this.db = firestoreInitializer.getDb();
        this.paymentCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }
    
    @Override
    public Payment getPaymentById(String id) {
        return paymentCache.get(id, key -> {
            try {
                DocumentSnapshot document = db.collection(PAYMENTS_COLLECTION).document(id).get().get();
                if (document.exists()) {
                    return document.toObject(Payment.class);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    
    @Override
    public Payment getPaymentByOrderId(String orderId) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(PAYMENTS_COLLECTION)
                    .whereEqualTo("orderId", orderId)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                Payment payment = documents.get(0).toObject(Payment.class);
                paymentCache.put(payment.getId(), payment);
                return payment;
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Payment savePayment(Payment payment) {
        try {
            // Generate ID if not present
            if (payment.getId() == null) {
                payment.setId(UUID.randomUUID().toString());
            }
            
            // Save to Firestore
            db.collection(PAYMENTS_COLLECTION).document(payment.getId()).set(payment).get();
            
            // Update cache
            paymentCache.put(payment.getId(), payment);
            
            return payment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Payment updatePayment(Payment payment) {
        return savePayment(payment);
    }
    
    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(PAYMENTS_COLLECTION)
                    .whereEqualTo("status", status)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Payment> payments = new ArrayList<>();
            
            for (QueryDocumentSnapshot document : documents) {
                Payment payment = document.toObject(Payment.class);
                payments.add(payment);
                paymentCache.put(payment.getId(), payment);
            }
            
            return payments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            // Convert LocalDateTime to Date for Firestore query
            Calendar calStart = Calendar.getInstance();
            calStart.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
            Date startDate = calStart.getTime();
            
            Calendar calEnd = Calendar.getInstance();
            calEnd.set(end.getYear(), end.getMonthValue() - 1, end.getDayOfMonth(), 23, 59, 59);
            Date endDate = calEnd.getTime();
            
            ApiFuture<QuerySnapshot> future = db.collection(PAYMENTS_COLLECTION)
                    .whereGreaterThanOrEqualTo("timestamp", startDate)
                    .whereLessThanOrEqualTo("timestamp", endDate)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Payment> payments = new ArrayList<>();
            
            for (QueryDocumentSnapshot document : documents) {
                Payment payment = document.toObject(Payment.class);
                payments.add(payment);
                paymentCache.put(payment.getId(), payment);
            }
            
            return payments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Payment> getPaymentsByMethod(String method) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(PAYMENTS_COLLECTION)
                    .whereEqualTo("method", method)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Payment> payments = new ArrayList<>();
            
            for (QueryDocumentSnapshot document : documents) {
                Payment payment = document.toObject(Payment.class);
                payments.add(payment);
                paymentCache.put(payment.getId(), payment);
            }
            
            return payments;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
