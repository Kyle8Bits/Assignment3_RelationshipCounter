package com.example.assignment3_relationshipcounter.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.assignment3_relationshipcounter.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class ForegroundService extends Service {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Set<String> processedDocumentIds = new HashSet<>();
    private boolean isInitialLoad = true;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "service_channel_id")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your app icon
                .setContentTitle("MonAmi")
                .setContentText("Welcome")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "service_channel_id",
                    "Foreground Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Start the foreground service with the notification
        startForeground(1, builder.build());

        setupFirestoreListener();
    }

    private void setupFirestoreListener() {
        db.collection("notifications").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("FirestoreService", "Listen failed.", e);
                return;
            }

            if (snapshots != null) {
                for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                    String documentId = documentChange.getDocument().getId();

                    // Only notify on new documents after the initial load
                    if (documentChange.getType() == DocumentChange.Type.ADDED && !processedDocumentIds.contains(documentId) && !isInitialLoad) {
                        Log.d("FirestoreService", "New document added: " + documentChange.getDocument().getData());

                        // Add document ID to processed list
                        processedDocumentIds.add(documentId);

                        // Trigger a notification
                        sendNotification(documentId, documentChange.getDocument().getData().toString());
                    }
                }

                // Mark the initial load as complete
                isInitialLoad = false;
            }
        });
    }

    private void sendNotification(String documentId, String documentData) {
        // Create and display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your actual icon
                .setContentTitle("New Notification")
                .setContentText("Document ID: " + documentId + ", Data: " + documentData)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Keep service running
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
