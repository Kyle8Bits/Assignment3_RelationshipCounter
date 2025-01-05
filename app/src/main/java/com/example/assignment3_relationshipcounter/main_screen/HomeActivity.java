package com.example.assignment3_relationshipcounter.main_screen;

import static com.example.assignment3_relationshipcounter.service.location.Location.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.assignment3_relationshipcounter.service.location.Location.stopLocationUpdates;
import static com.example.assignment3_relationshipcounter.service.location.Location.updateUserPosition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.adapter.StoryList;
import com.example.assignment3_relationshipcounter.service.location.Location;
import com.example.assignment3_relationshipcounter.service.models.User;

public class HomeActivity extends AppCompatActivity {
    private User currentUser;
    Button friend, searchFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateUserLocation();
        loadingAppUi();
        loadingFriendListUI();
        loadingStoryList();

    }

    private void loadingAppUi() {
        friend = findViewById(R.id.home_friendlist);
        friend.setSelected(true);
    }

    private void loadingFriendListUI() {
        FriendList adapter = new FriendList(this);
        RecyclerView recyclerView = findViewById(R.id.home_friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadingStoryList() {
        StoryList adapter = new StoryList(this);
        RecyclerView recyclerView = findViewById(R.id.home_story_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void updateUserLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updateUserPosition(this, location -> {
                Toast.makeText(
                        this,
                        "Location updated: " + location.getLatitude() + ", " + location.getLongitude(),
                        Toast.LENGTH_SHORT
                ).show();

            });
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (this.isFinishing()) {
//            stopLocationUpdates(LocationServices.getFusedLocationProviderClient(this));
//        }
//    }
}