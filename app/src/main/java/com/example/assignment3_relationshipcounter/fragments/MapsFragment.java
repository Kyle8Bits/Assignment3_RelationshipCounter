package com.example.assignment3_relationshipcounter.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.CreateChatRoomList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    private final DataUtils dataUtils = new DataUtils();
    private RecyclerView searchPeople;
    private CreateChatRoomList resultAdapter;
    private FusedLocationProviderClient client;

    private TextInputEditText search_bar;

    public MapsFragment() {

    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                com.example.assignment3_relationshipcounter.service.permission.Location.requestLocationPermissions(requireActivity());
            } else {
                getPosition(googleMap);
                setUpFriendLocation(googleMap);
                friendSetOnMarker(googleMap);
                setupSearchResult(googleMap);
            }

        }
    };

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        searchPeople = view.findViewById(R.id.search_bar);
        search_bar = view.findViewById(R.id.et_search_friend);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    void setupSearchResult(GoogleMap gMap) {
        resultAdapter = new CreateChatRoomList(requireContext(), user -> {
            // Handle user click, e.g., open chat with the user
            LatLng currentLocation = new LatLng(user.getLatitude(), user.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        });

        searchPeople.setLayoutManager(new LinearLayoutManager(getContext()));
        searchPeople.setAdapter(resultAdapter);


        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.isEmpty()) {
                    searchPeople.setVisibility(View.GONE); // Hide search results
                    resultAdapter.updateUserList(new ArrayList<>()); // Clear the search adapter
                } else {
                    searchPeople.setVisibility(View.VISIBLE);
                    performSearch(query, resultAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void performSearch(String query, CreateChatRoomList resultAdapter) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.getFriendsOfUser(new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                List<User> filteredUsers = new ArrayList<>();
                for (User user : data) {
                    if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }
                if (!filteredUsers.isEmpty()) {
                    resultAdapter.updateUserList(filteredUsers);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    public void setUpFriendLocation(GoogleMap gMap) {
        dataUtils.getFriendsOfUser(new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                if (!data.isEmpty()) {
                    for (User user : data) {
                        LatLng userLocation = new LatLng(user.getLatitude(), user.getLongitude());
                        Glide.with(requireContext())
                                .asBitmap()
                                .load(user.getAvatarUrl())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        // Create a custom marker with the loaded image
                                        BitmapDescriptor customMarker = createCustomMarker(resource, Color.BLUE);
                                        Marker marker = gMap.addMarker(new MarkerOptions()
                                                .position(userLocation)
                                                .icon(customMarker)
                                                .title(user.getUsername()));

                                        // Attach the user object as a tag
                                        marker.setTag(user);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Handle placeholder if needed
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "No friends found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    //Move to the profile when click on user
    public void friendSetOnMarker(GoogleMap gMap) {
        gMap.setOnMarkerClickListener(marker -> {
            User user = (User) marker.getTag();

            /**
             * Put the user' id as intent Extra then start profile activity and get the information from Firebase
             */
            Toast.makeText(
                    getContext(),
                    "Friend: " + user.getUsername(),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        });
    }

    private BitmapDescriptor createCustomMarker(Bitmap profileImage, int circleColor) {
        int markerSize = 150; // Marker size in pixels
        Bitmap bitmap = Bitmap.createBitmap(markerSize, markerSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw a larger circle for the background with a 5px extra margin
        Paint circlePaint = new Paint();
        circlePaint.setColor(circleColor); // Circle background color
        circlePaint.setStyle(Paint.Style.FILL);

        float circleRadius = markerSize / 2f; // Circle radius
        canvas.drawCircle(markerSize / 2f, markerSize / 2f, circleRadius, circlePaint);

        // Draw the profile image with a smaller radius (5px margin)
        Bitmap circularImage = cropToCircle(profileImage, (int) (circleRadius - 10)); // Subtract 5px for margin
        int left = (int) ((markerSize - circularImage.getWidth()) / 2f);
        int top = (int) ((markerSize - circularImage.getHeight()) / 2f);
        canvas.drawBitmap(circularImage, left, top, null);

        // Convert the bitmap to a BitmapDescriptor for the marker
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private Bitmap cropToCircle(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // Set up a circular clipping path
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius, paint);

        // Use SRC_IN to crop the image to the circle
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = new Rect(0, 0, radius * 2, radius * 2);
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return output;
    }

    @SuppressLint("MissingPermission")
    public void getPosition(GoogleMap mMap) {
        client = LocationServices.getFusedLocationProviderClient(getContext());
        client.getLastLocation().addOnSuccessListener(location -> {
            try {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });
    }
}