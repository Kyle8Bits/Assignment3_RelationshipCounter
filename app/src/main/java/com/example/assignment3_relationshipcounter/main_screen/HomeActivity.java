package com.example.assignment3_relationshipcounter.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.adapter.StoryList;
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


        loadingAppUi();
        loadingFriendListUI();
        loadingStoryList();

        Intent intent = getIntent();
        if (intent.getSerializableExtra("currentUser") != null) {
            currentUser = (User) intent.getSerializableExtra("currentUser");
        }

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
}