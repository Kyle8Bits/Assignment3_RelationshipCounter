package com.example.assignment3_relationshipcounter.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            // Get the current battery level
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            Log.d("BatteryLevelReceiver", "Battery level: " + batteryPct + "%");

            // Perform action if battery level is 10%
            if (batteryPct == 10) {
                Toast.makeText(context, "Battery is 10%! Charge your phone now", Toast.LENGTH_SHORT).show();

                // Example: Trigger a call broadcast
                Intent callIntent = new Intent("com.example.CALL_PERSON");
                callIntent.putExtra("phone_number", "0938202272"); // Replace with your phone number
                context.sendBroadcast(callIntent);
            }
        }
    }
}