package com.example.assignment3_relationshipcounter.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.fragments.ChatFragment;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ForegroundService extends Service {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean isInitialLoadFriend = true;
    private boolean isInitialLoadChat = true;
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
        setupChatListener();
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

                        System.out.println(firstUser + "-" + secondUser);
                        String otherUserId = Utils.getOtherId(user.getUid(), usersId); // Ensure usersId has data
                        String[] sentUserId = new String[1];
                        if (check != null) {
                            if (check) {
                                message[0] = " has accepted your friend request";
                                sentUserId[0] = firstUser;
                            } else {

                                message[0] = " has sent you a friend request";
                                sentUserId[0] = secondUser;
                            }
                        }
                        if (!isInitialLoadFriend) {
                            dataUtils.getById("users", otherUserId, User.class, new DataUtils.FetchCallback<User>() {
                                @Override
                                public void onSuccess(User data) {
                                    String notification = data.getFirstName() + " " + data.getLastName() + message[0];
                                    String title = "You have a new friend";
                                    sendNotification(false, null, sentUserId[0], notification, title);

                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("FirestoreService", "Failed to fetch user data", e);
                                }
                            });
                        }
                    }
                }
                isInitialLoadFriend = false;
            }
        });
    }

    private void setupChatListener() {
        CollectionReference chatroomsCollection = db.collection("chatrooms");

        Query query = chatroomsCollection.whereArrayContains("userIds", user.getUid());

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }

            // Ensure that the snapshots are not null and contain data
            if (snapshots != null) {
                for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                        Map<String, Object> documentData = documentChange.getDocument().getData();
                        String lastMessage = (String) documentData.get("lastMessage");
                        String lastMessageSenderId = (String) documentData.get("lastMessageSenderId");
                        String roomId = (String) documentData.get("chatroomId");

                        if(!lastMessageSenderId.equals(user.getUid())){
                            dataUtils.getById("users", lastMessageSenderId, User.class, new DataUtils.FetchCallback<User>() {
                                @Override
                                public void onSuccess(User data) {
                                    if (!isInitialLoadChat) {
                                        String notification = data.getUsername() + ": " + lastMessage;
                                        String title = "You have new message";
                                        sendNotification(true,data,user.getUid(), notification, title);
                                    }
                                    else {
                                        System.out.println("first chat");
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                    }
                }
                isInitialLoadChat = false;
            }
        });
    }

    private void sendNotification(Boolean isChat, User userOther,String sentToId, String notification, String title) {
        String currentUid = user.getUid();
        PendingIntent pendingIntent;
        if (sentToId.equals(currentUid)) {
            if(isChat) {
                Intent intent = new Intent(this, ChatFragment.class);  // Replace ChatActivity with the activity you want to open
                intent.putExtra("otherUser", userOther);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                Intent intent = new Intent(this, HomeActivity.class);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            }
        // Create and display the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification) // Replace with your actual icon
                .setContentTitle(title)
                .setContentText(notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

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
