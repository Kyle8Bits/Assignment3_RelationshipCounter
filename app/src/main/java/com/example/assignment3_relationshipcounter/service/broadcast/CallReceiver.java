package com.example.assignment3_relationshipcounter.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallReceiver", "Broadcast received"); // Log to check if the receiver is triggered

        if (intent != null && "com.example.CALL_PERSON".equals(intent.getAction())) {
            String phoneNumber = intent.getStringExtra("phone_number");
            Log.d("CallReceiver", "Phone number: " + phoneNumber);
            if (phoneNumber != null) {
                Log.d("CallReceiver", "Phone number: " + phoneNumber);

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (context.checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(callIntent);
                } else {
                    Log.e("CallReceiver", "CALL_PHONE permission not granted.");
                }
            } else {
                Log.e("CallReceiver", "Phone number is null.");
            }
        }
    }
}
