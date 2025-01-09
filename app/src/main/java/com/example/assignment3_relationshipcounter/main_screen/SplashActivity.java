package com.example.assignment3_relationshipcounter.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.assignment3_relationshipcounter.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        //TODO: Finish this function : Click on a notification, direct to the chat room of the user who sent the notification
        if(getIntent().getExtras() != null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");

        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                    finish();
                }
            },2000);
        }
    }
}