package com.example.assignment3_relationshipcounter.service.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class Location {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Requests location permissions if not already granted.
     *
     * @param activity The activity from which this method is called.
     */
    public static void requestLocationPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Handles the result of the permission request.
     *
     * @param requestCode  The request code passed in requestPermissions.
     * @param grantResults The results of the permission request.
     * @param activity     The activity where the result is handled.
     */
    public static void handlePermissionResult(int requestCode, @NonNull int[] grantResults, Activity activity) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(activity, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void getPosition(Context context) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        LatLng lastLocation = new LatLng(37.4219983, -122.084);
        // Create a location request
        LocationRequest locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, // High accuracy
                5000 // Interval in milliseconds
        ).setMinUpdateIntervalMillis(2000) // Fastest interval
                .build();

        // Create a location callback to handle location updates
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (android.location.Location location : locationResult.getLocations()) {
                    try {
                        // Get the updated position
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if(currentLocation.latitude != lastLocation.latitude || currentLocation.longitude != lastLocation.longitude ) {
                            System.out.println(currentLocation.latitude + currentLocation.longitude);
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };

        // Request location updates
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

}
