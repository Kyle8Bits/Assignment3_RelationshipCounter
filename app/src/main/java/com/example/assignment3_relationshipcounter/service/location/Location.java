package com.example.assignment3_relationshipcounter.service.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Location {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static LocationCallback locationCallback;
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
    public static void updateUserPosition(Context context ,LocationUpdateListener listener) {
        DataUtils dataUt = new DataUtils();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        // Create a location request
        LocationRequest locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, // High accuracy
                5000 // Interval in milliseconds
        ).setMinUpdateIntervalMillis(2000) // Fastest interval
                .build();

        // Create a location callback to handle location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (android.location.Location location : locationResult.getLocations()) {
                    try {
                        Map<String, Object> fieldsToUpdate = new HashMap<>();
                        fieldsToUpdate.put("latitude", location.getLatitude());
                        fieldsToUpdate.put("longitude", location.getLongitude());
                        dataUt.updateOneFieldById(
                                "users",
                                new Authentication().getUserDetail().getId(),
                                fieldsToUpdate,
                                new DataUtils.NormalCallback<Void>() {
                                    @Override
                                    public void onSuccess() {
                                        listener.onLocationUpdated(location);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("FirestoreUpdate", "Failed to update fields", e);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };

        // Request location updates
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public static void stopLocationUpdates(FusedLocationProviderClient client) {
        if (locationCallback != null) {
            client.removeLocationUpdates(locationCallback);
            Log.d("LocationUpdate", "Location updates stopped");
        }
    }

    public interface LocationUpdateListener {
        void onLocationUpdated(android.location.Location location);
    }
}
