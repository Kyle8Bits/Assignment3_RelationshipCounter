package com.example.assignment3_relationshipcounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationBR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals("com.example.NOTIFICATION_RECEIVED")) {
            // Retrieve data from the intent if needed
            String documentName = intent.getStringExtra("document_id");

            // Create and display the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.ic_notification) // Replace with your actual icon
                    .setContentTitle("New Notification")
                    .setContentText("Document name: " + documentName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Create notification channel for Android 8.0+
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
    }
}

