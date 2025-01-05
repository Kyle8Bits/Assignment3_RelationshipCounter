package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendList adapter;
    private List<User> allNonFriends; // List of all non-friends
    private TextInputEditText searchField;
    private Button searchButton;
    private DataUtils dataUtils;
    private String currentUserId;
    private MaterialButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friend, container, false);

        // Log all relationships
        logAllRelationships();

        // Initialize Firebase and utility classes
        dataUtils = new DataUtils();
        currentUserId = FirebaseAuth.getInstance().getUid();

        // Initialize UI components
        backButton = view.findViewById(R.id.back_button);
        searchField = view.findViewById(R.id.et_search_friend);
        searchButton = view.findViewById(R.id.btn_search_friend);
        recyclerView = view.findViewById(R.id.rv_friend_list);

        // Set up back button
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Initialize the adapter and list
        allNonFriends = new ArrayList<>();
        adapter = new FriendList(requireContext(), new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Load non-friend users on fragment load
        loadNonFriendUsers();

        // Set up search button to filter the non-friend list
        searchButton.setOnClickListener(v -> filterNonFriendList());

        return view;
    }

    private void loadNonFriendUsers() {
        dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> allUsers) {
                dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
                    @Override
                    public void onSuccess(List<Relationship> relationships) {
                        List<String> friendIds = new ArrayList<>();
                        for (Relationship relationship : relationships) {
                            if (relationship.getStatus() == FriendStatus.FRIEND) {
                                if (relationship.getFirstUser().equals(currentUserId)) {
                                    friendIds.add(relationship.getSecondUser());
                                } else if (relationship.getSecondUser().equals(currentUserId)) {
                                    friendIds.add(relationship.getFirstUser());
                                }
                            }
                        }

                        for (User user : allUsers) {
                            if (!user.getId().equals(currentUserId)) {
                                fetchRelationshipForUser(user, new DataUtils.FetchCallback<Relationship>() {
                                    @Override
                                    public void onSuccess(Relationship relationship) {
                                        if (!friendIds.contains(user.getId())) {
                                            allNonFriends.add(user);
                                        }
                                        adapter.updateList(allNonFriends);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("SearchFriendFragment", "Failed to fetch relationship for user: " + user.getId(), e);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), "Failed to load relationships", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void fetchRelationshipForUser(User user, DataUtils.FetchCallback<Relationship> callback) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                for (Relationship rel : relationships) {
                    if ((rel.getFirstUser().equals(user.getId()) && rel.getSecondUser().equals(UserSession.getInstance().getCurrentUser().getId())) ||
                            (rel.getSecondUser().equals(user.getId()) && rel.getFirstUser().equals(UserSession.getInstance().getCurrentUser().getId()))) {
                        callback.onSuccess(rel);
                        return;
                    }
                }

                // No relationship found; default to NOT_FRIEND
                Relationship defaultRelationship = new Relationship(
                        "rel_" + user.getId(),
                        UserSession.getInstance().getCurrentUser().getId(),
                        user.getId(),
                        new Utils().getCurrentDate(),
                        null,
                        FriendStatus.NOT_FRIEND,
                        0
                );
                callback.onSuccess(defaultRelationship);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    private void filterNonFriendList() {
        String query = searchField.getText().toString().trim();

        if (query.isEmpty()) {
            // Reset the list to show all non-friends
            adapter.updateList(allNonFriends);
        } else {
            List<User> filteredUsers = new ArrayList<>();
            for (User user : allNonFriends) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }

            if (filteredUsers.isEmpty()) {
                Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show();
            }

            // Update adapter with the filtered list
            adapter.updateList(filteredUsers);
        }
    }

    private void logAllRelationships() {
        DataUtils dataUtils = new DataUtils();
        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                for (Relationship relationship : relationships) {
                    Log.d("RelationshipLog", "ID: " + relationship.getId() +
                            ", FirstUser: " + relationship.getFirstUser() +
                            ", SecondUser: " + relationship.getSecondUser() +
                            ", Status: " + relationship.getStatus() +
                            ", DateCreated: " + relationship.getDateCreated() +
                            ", DateAccepted: " + relationship.getDateAccept());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RelationshipLog", "Failed to fetch relationships", e);
            }
        });
    }

}
