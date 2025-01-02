package com.example.assignment3_relationshipcounter.main_screen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;

public class WelcomeActivity extends AppCompatActivity {
    DataUtils dataUtils = new DataUtils();
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


        /**
         * This is for NOTIFICATION function
         * delete the if wrap then...
         * replace the "123" with current login user's id before start new activity
         */
        if(false) {
            dataUtils.getFCMToken("123");
        }
    }

}