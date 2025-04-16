package com.example.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

/**
 * Common service for initializing Firestore database connection.
 * This class is moved to the common module to be reused across the application.
 */
@Service
public class FirestoreInitializer {

    @Value("${gcp.projectId}")
    private String projectId;

    private Firestore db;

    /**
     * Initialize Firestore using default project ID.
     */
    public FirestoreInitializer() throws Exception {
        System.out.println("initialising Firestore db");
        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId(projectId)
                        .build();
        Firestore db = firestoreOptions.getService();
        this.db = db;
        System.out.println("initialisation complete of Firestore DB");
    }

    /**
     * Get firestore db
     * @return Firestore instance
     */
    public Firestore getDb() {
        return db;
    }
}
