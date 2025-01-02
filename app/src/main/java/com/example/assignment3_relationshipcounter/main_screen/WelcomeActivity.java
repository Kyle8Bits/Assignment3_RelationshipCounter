package com.example.assignment3_relationshipcounter.main_screen;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.models.Gender;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.models.UserType;

public class WelcomeActivity extends AppCompatActivity {
    Authentication auth = new Authentication();
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

        User user = new User(null, "Doris", "Doris", "Doris@gmail.com", "27/01/2003", Gender.MALE, "0938223211", UserType.USER);
        auth.registerNewUser("Doris@gmail.com", "123456", user, new Authentication.RegisterNewUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(
                        WelcomeActivity.this,
                        "Register new user success",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }


}