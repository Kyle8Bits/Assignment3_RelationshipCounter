package com.example.assignment3_relationshipcounter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.adapter.StoryList;
import com.example.assignment3_relationshipcounter.service.models.User;

public class HomeFragment extends Fragment {
    private User currentUser;
    private Button friend, searchFriend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI
        loadingAppUi(view);
        loadingFriendListUI(view);
        loadingStoryList(view);

        // Retrieve current user from arguments (if any)
        if (getArguments() != null && getArguments().getSerializable("currentUser") != null) {
            currentUser = (User) getArguments().getSerializable("currentUser");
        }

        return view;
    }

    private void loadingAppUi(View view) {
        friend = view.findViewById(R.id.home_friendlist);
        friend.setSelected(true);
    }

    private void loadingFriendListUI(View view) {
        FriendList adapter = new FriendList(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.home_friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadingStoryList(View view) {
        StoryList adapter = new StoryList(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.home_story_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}
