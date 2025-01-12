package com.example.assignment3_relationshipcounter.main_screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.fragments.LoginFragment;
import com.example.assignment3_relationshipcounter.fragments.SignUpFragment;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.stripe.android.PaymentConfiguration;

public class WelcomeActivity extends AppCompatActivity {

    // Check if the user is current signed in
    @Override
    public void onStart() {
        super.onStart();
        DataUtils dataUtils = new DataUtils();
        Authentication auth = new Authentication();
        FirebaseUser user = auth.getAuth().getCurrentUser();
        if (user != null) {
            user.reload();
            // Get the user information
            dataUtils.getById("users", user.getUid(), User.class, new DataUtils.FetchCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                    intent.putExtra("currentUser", user);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51QgNo7RopI2ofz188cRxyDxL0S0XaR0wuydbRO8pkoA6tY8KnQzis9JrZSvAxdI7IEDqaGze7PmM2oc7NAt35Ib800Fe4rvQ8s"
        );
        Context context = getApplicationContext();
        String packageName = context.getPackageName();
        Log.d("PackageName", "App Package Name: " + packageName);
        Intent intent = getIntent();
        FragmentContainerView authContainer = findViewById(R.id.auth_container);
        Button toLoginBtn = findViewById(R.id.button_to_login);
        toLoginBtn.setOnClickListener(v -> {
            replaceFragment(new LoginFragment());
            authContainer.setVisibility(View.VISIBLE);
        });
        Button toSignUpBtn = findViewById(R.id.button_to_signup);
        toSignUpBtn.setOnClickListener(v -> {
            replaceFragment(new SignUpFragment());
            authContainer.setVisibility(View.VISIBLE);
        });
    }


    /**
     * Replace fragment in the container
     *
     * @param fragment either login or signup fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.auth_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}