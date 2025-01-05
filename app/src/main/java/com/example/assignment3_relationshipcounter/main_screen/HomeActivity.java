package com.example.assignment3_relationshipcounter.main_screen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.assignment3_relationshipcounter.R;
//import com.example.assignment3_relationshipcounter.fragments.FriendsFragment;
import com.example.assignment3_relationshipcounter.fragments.SearchFriendFragment;
import com.example.assignment3_relationshipcounter.fragments.HomeFragment;
//import com.example.assignment3_relationshipcounter.fragments.ProfileFragment;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity {

    private User currentUser; // Store the current user object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    // Load HomeFragment
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_discover:
                    selectedFragment = new SearchFriendFragment();
                    break;

                case R.id.nav_add:
//                    selectedFragment = new ProfileFragment();
                    break;

                case R.id.nav_friends:
//                    selectedFragment = new ProfileFragment();
                    break;

                case R.id.nav_chat:
//                    selectedFragment = new ProfileFragment();
                    break;
                default:
                    // Handle unexpected cases gracefully
                    selectedFragment = new HomeFragment(); // Fallback to HomeFragment
                    break;
            }


            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

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

    /**
     * Get the current user object.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
