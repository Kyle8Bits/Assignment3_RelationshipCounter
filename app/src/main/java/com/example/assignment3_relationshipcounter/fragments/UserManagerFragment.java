package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagerFragment extends Fragment {

    private RecyclerView userRecyclerView;
    private FriendList userAdapter;
    private List<User> userList;
    private DataUtils dataUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);

        // Initialize DataUtils
        dataUtils = new DataUtils();

        // Initialize RecyclerView and Adapter
        userRecyclerView = view.findViewById(R.id.user_manager_recycler_view);
        userList = new ArrayList<>();
        userAdapter = new FriendList(requireContext(), userList);

        // Configure RecyclerView
        userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userRecyclerView.setAdapter(userAdapter);

        // Fetch all users
        fetchAllUsers();

        return view;
    }

    private void fetchAllUsers() {
        dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userList.clear();
                userList.addAll(users);
                userAdapter.updateList(userList);
                userAdapter.notifyDataSetChanged();

                // Log for debugging
                Log.d("UserManagerFragment", "Loaded " + users.size() + " users.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserManagerFragment", "Failed to fetch users", e);
            }
        });
    }
}
