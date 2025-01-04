package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendList adapter;
    private List<User> userList;
    private EditText searchField;
    private Button searchButton;
    private DataUtils dataUtils;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        // Initialize Firebase and utility classes
        dataUtils = new DataUtils();
        currentUserId = FirebaseAuth.getInstance().getUid();

        // Initialize UI components
        searchField = view.findViewById(R.id.et_search_friend);
        searchButton = view.findViewById(R.id.btn_search_friend);
        recyclerView = view.findViewById(R.id.rv_friend_list);

        // Initialize the user list and adapter
        userList = new ArrayList<>();
        adapter = new FriendList(requireContext(), userList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Load users who are not friends
        loadNonFriendUsers();

        // Set up search button
        searchButton.setOnClickListener(v -> searchUsers());

        return view;
    }

    private void loadNonFriendUsers() {
        userList.clear();
        dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> allUsers) {
                dataUtils.getAll("relationships", com.example.assignment3_relationshipcounter.service.models.Relationship.class,
                        new DataUtils.FetchCallback<List<com.example.assignment3_relationshipcounter.service.models.Relationship>>() {
                            @Override
                            public void onSuccess(List<com.example.assignment3_relationshipcounter.service.models.Relationship> relationships) {
                                // Filter users who are not friends
                                for (User user : allUsers) {
                                    if (!user.getId().equals(currentUserId) && !isFriend(user.getId(), relationships)) {
                                        userList.add(user);
                                    }
                                }
                                adapter.updateList(userList);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isFriend(String userId, List<com.example.assignment3_relationshipcounter.service.models.Relationship> relationships) {
        for (com.example.assignment3_relationshipcounter.service.models.Relationship relationship : relationships) {
            if ((relationship.getFirstUser().equals(currentUserId) && relationship.getSecondUser().equals(userId)) ||
                    (relationship.getSecondUser().equals(currentUserId) && relationship.getFirstUser().equals(userId))) {
                return relationship.getStatus() == FriendStatus.FRIEND;
            }
        }
        return false;
    }

    private void searchUsers() {
        String query = searchField.getText().toString().trim();

        if (query.isEmpty()) {
            loadNonFriendUsers();
        } else {
            List<User> filteredUsers = new ArrayList<>();
            for (User user : userList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
            adapter.updateList(filteredUsers);
        }
    }
}
