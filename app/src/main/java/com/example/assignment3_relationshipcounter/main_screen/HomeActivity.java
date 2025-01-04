package com.example.assignment3_relationshipcounter.main_screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.assignment3_relationshipcounter.R;
//import com.example.assignment3_relationshipcounter.fragments.FriendsFragment;
import com.example.assignment3_relationshipcounter.fragments.DiscoverFragment;
import com.example.assignment3_relationshipcounter.fragments.HomeFragment;
//import com.example.assignment3_relationshipcounter.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.assignment3_relationshipcounter.R;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                    selectedFragment = new DiscoverFragment();
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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
