package com.example.assignment3_relationshipcounter.main_screen;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.assignment3_relationshipcounter.R;
//import com.example.assignment3_relationshipcounter.fragments.FriendsFragment;
import com.example.assignment3_relationshipcounter.fragments.ChatRoomFragment;
import com.example.assignment3_relationshipcounter.fragments.MapsFragment;
import com.example.assignment3_relationshipcounter.fragments.ProfileFragment;
import com.example.assignment3_relationshipcounter.fragments.SearchFriendFragment;
import com.example.assignment3_relationshipcounter.fragments.HomeFragment;
//import com.example.assignment3_relationshipcounter.fragments.ProfileFragment;
import com.example.assignment3_relationshipcounter.fragments.SupportFragment;
import com.example.assignment3_relationshipcounter.fragments.UserManagerFragment;
import com.example.assignment3_relationshipcounter.service.ForegroundService;
import com.example.assignment3_relationshipcounter.service.broadcast.BatteryReceiver;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.UserType;
import com.example.assignment3_relationshipcounter.service.permission.Location;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.permission.Notification;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity {
    private User currentUser; // Store the current user object
    private BatteryReceiver batteryLevelReceiver;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("NotificationPermission", "Notification permission granted.");
                } else {
                    Log.d("NotificationPermission", "Notification permission denied.");
                }

                // After handling notification permission, check location permission
                requestLocationPermission();
            });

    private DataUtils dataUtils = new DataUtils();

    @Override
    protected void onStart() {
        super.onStart();

        // Step 1: Request Notification Permission First (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Notification notificationHandler = new Notification(this);
            if (!notificationHandler.isNotificationPermissionGranted()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Show rationale for notification permission
                    Toast.makeText(this, "Please enable notification permission to receive alerts.", Toast.LENGTH_LONG).show();
                } else {
                    notificationHandler.requestNotificationPermission(this, notificationPermissionLauncher);
                    return; // Exit here to wait for notification permission result
                }
            }
        }

        // Step 2: If Notification Permission is Already Granted, Check Location Permission
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Location.requestLocationPermissions(this);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        Location.updateUserPosition(this);
        setUpReceiverBattery();
        setContentView(R.layout.activity_home);

        // Fetch user from Intent or Session
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        // Save user to UserSession for global access
        if (currentUser != null) {
            UserSession.getInstance().setCurrentUser(currentUser);
        } else {
            fetchUserData(); // Fetch user data if not passed
        }

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configure navigation menu based on user type
        configureBottomNavigation(bottomNavigationView);

        // Set default fragment
        // Set default fragment based on user type
        if (savedInstanceState == null) {
            if (currentUser != null && currentUser.getAccountType() == UserType.ADMIN) {
                loadFragment(new UserManagerFragment()); // Default fragment for admin
                bottomNavigationView.setSelectedItemId(R.id.nav_user_manager); // Highlight User Manager tab
            } else {
                loadFragment(new HomeFragment()); // Default fragment for normal users
                bottomNavigationView.setSelectedItemId(R.id.nav_home); // Highlight Home tab
            }
        }


        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (currentUser.getAccountType() == UserType.ADMIN) {
                switch (item.getItemId()) {
                    case R.id.nav_user_manager:
                        selectedFragment = new UserManagerFragment();
                        break;
                    case R.id.nav_support:
                        selectedFragment = new ChatRoomFragment();
                        break;
                    case R.id.nav_profile:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", currentUser);
                        selectedFragment = new ProfileFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    default:
                        selectedFragment = new UserManagerFragment();
                        break;
                }
            } else {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_discover:
                        if (currentUser != null && currentUser.getAccountType() == UserType.USER) {
                            Toast.makeText(HomeActivity.this, "This feature is available for Premium users only.", Toast.LENGTH_SHORT).show();
                            return false; // Prevent navigation
                        }
                        selectedFragment = new MapsFragment();
                        break;
                    case R.id.nav_profile:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", currentUser);
                        selectedFragment = new ProfileFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.nav_friends:
                        selectedFragment = new SearchFriendFragment();
                        break;
                    case R.id.nav_chat:
                        if (currentUser != null && currentUser.getAccountType() == UserType.USER) {
                            Toast.makeText(HomeActivity.this, "This feature is available for Premium users only.", Toast.LENGTH_SHORT).show();
                            return false; // Prevent navigation
                        }
                        selectedFragment = new ChatRoomFragment();
                        break;
                    default:
                        selectedFragment = new HomeFragment();
                        break;
                }
            }

            loadFragment(selectedFragment);
            return true;
        });

    }

    private void configureBottomNavigation(BottomNavigationView bottomNavigationView) {
        if (currentUser.getAccountType() == UserType.ADMIN) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.admin_navigation_menu);
        } else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Location.handlePermissionResult(requestCode, grantResults, this);
    }

    /**
     * Load the specified fragment.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Fetch user data from Firestore if not passed via Intent.
     */
    private void fetchUserData() {
        String userId = getIntent().getStringExtra("userId"); // Assume userId is passed
        if (userId == null) return;

        DataUtils dataUtils = new DataUtils();
        dataUtils.getById("users", userId, User.class, new DataUtils.FetchCallback<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                UserSession.getInstance().setCurrentUser(user); // Save user to session
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error (e.g., show error message)
                e.printStackTrace();
            }
        });
    }

    private void setUpReceiverBattery(){
        batteryLevelReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, filter);
    }
    /**
     * Get the current user object.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
