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
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ForegroundService extends Service {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Set<String> processedDocumentIds = new HashSet<>();
    private boolean isInitialLoad = true;
    Authentication auth = new Authentication();
    FirebaseUser user = auth.getAuth().getCurrentUser();
    DataUtils dataUtils = new DataUtils();


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

        setupAcceptFriendListener();
    }

    private void setupAcceptFriendListener() {
        db.collection("relationships").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("FirestoreService", "Listen failed for relationships.", e);
                return;
            }
            if (snapshots != null) {
                for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                    // Listen for updated documents
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED || documentChange.getType() == DocumentChange.Type.ADDED) {
                        Map<String, Object> documentData = documentChange.getDocument().getData();
                        List<String> usersId = new ArrayList<>();
                        final String[] message = {""};
                        Boolean check = (Boolean) documentData.get("friend");
                        String firstUser = (String) documentData.get("firstUser");
                        String secondUser = (String) documentData.get("secondUser");
                        // Check if the friend field is true
                        if (firstUser != null && secondUser != null) {
                            usersId.add(firstUser);
                            usersId.add(secondUser);
                        }

                        String otherUserId = Utils.getOtherId(user.getUid(), usersId); // Ensure usersId has data

                        if(check!=null){
                            if (check){
                                message[0] = " has accepted your friend request";
                            }
                            else {
                                message[0] = " has sent you a friend request";
                            }
                        }

                        dataUtils.getById("users", otherUserId, User.class, new DataUtils.FetchCallback<User>() {
                            @Override
                            public void onSuccess(User data) {
                                if(!isInitialLoad){
                                String notification = data.getFirstName() + " " + data.getLastName() + message[0];
                                String title = "You have a new friend";
                                sendNotification(otherUserId, notification, title);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("FirestoreService", "Failed to fetch user data", e);
                            }
                        });
                    }
                }
                isInitialLoad = false;
            }
        });
    }

    private void setUpAddFriendListener(){
        db.collection("relationships").addSnapshotListener((snapshots, e) -> {
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
                    }
                }

                // Mark the initial load as complete
                isInitialLoad = false;
            }
        });

    }

    private void sendNotification(String sentToId, String notification, String title) {
        String currentUid = user.getUid();

        if (sentToId.equals(currentUid)) {

        // Create and display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your actual icon
                .setContentTitle(title)
                .setContentText(notification)
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
        } else {
            // Log or handle the case when sentToId does not match the current user
            Log.d("sendNotification", "Notification not sent: sentToId does not match current user.");
        }
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
