package com.example.assignment3_relationshipcounter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.ChatRoomList;
import com.example.assignment3_relationshipcounter.adapter.CreateChatRoomList;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.ChatRoom;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {

    ChatRoomList adapter;
    CreateChatRoomList resultAdapter;
    RecyclerView recyclerView, createChatRCV;
    DataUtils dataUtils = new DataUtils();
    TextInputEditText searchbar;

    public ChatRoomFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room,container,false);
        recyclerView = view.findViewById(R.id.chat_room_created);
        searchbar = view.findViewById(R.id.et_search_friend);
        createChatRCV = view.findViewById(R.id.create_chat_rcv);


        setupRecyclerView();
        setupSearchResult();
        return view;
    }
    void setupRecyclerView(){

        Query query = dataUtils.getAllChatRoomOfUser();

        FirestoreRecyclerOptions<ChatRoom> options = new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query,ChatRoom.class).build();

        adapter = new ChatRoomList(options,getContext(), user -> {
            Intent intent = new Intent(requireActivity(), ChatFragment.class);
            intent.putExtra("otherUser", user);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    void setupSearchResult(){

        resultAdapter = new CreateChatRoomList(requireContext(), user -> {
            // Handle user click, e.g., open chat with the user
            Intent intent = new Intent(requireActivity(), ChatFragment.class);
            intent.putExtra("otherUser", user);
            startActivity(intent);
        });

        createChatRCV.setLayoutManager(new LinearLayoutManager(getContext()));
        createChatRCV.setAdapter(resultAdapter);


        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.isEmpty()) {
                    createChatRCV.setVisibility(View.GONE); // Hide search results
                    recyclerView.setVisibility(View.VISIBLE); // Show original chat rooms
                    resultAdapter.updateUserList(new ArrayList<>()); // Clear the search adapter
                } else {
                    recyclerView.setVisibility(View.GONE); // Hide original chat rooms
                    createChatRCV.setVisibility(View.VISIBLE); // Show search results
                    performSearch(query, resultAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });


    }

    private void performSearch(String query, CreateChatRoomList resultAdapter) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.getFriendsOfUser(new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                List<User> filteredUsers = new ArrayList<>();
                for (User user : data) {
                    if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }
                if (!filteredUsers.isEmpty()) {
                    resultAdapter.updateUserList(filteredUsers);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}