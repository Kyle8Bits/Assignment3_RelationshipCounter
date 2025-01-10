package com.example.assignment3_relationshipcounter.service.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Notification {
    private final Context context;

    // Constructor to initialize with context
    public Notification(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Checks if notification permission is granted.
     * @return true if permission is granted, false otherwise.
     */
    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No need to request permission below Android 13
    }

    /**
     * Requests the notification permission.
     * @param activity The activity that will handle the permission request.
     * @param permissionLauncher The launcher registered to handle the result.
     */
    public void requestNotificationPermission(Activity activity, ActivityResultLauncher<String> permissionLauncher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (!isNotificationPermissionGranted()) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
