package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.models.UserType;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private ImageView avatar;
    private TextView username, firstName, lastName, gender, dob, totalFriendsTV;
    private Button actionBtn, deleteBtn;
    private RecyclerView friendsRecyclerView;

    private User user;
    private DataUtils dataUtils;
    private String currentUserId;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize UI components
        ImageView backButton = view.findViewById(R.id.backButton);
        avatar = view.findViewById(R.id.avatar);
        username = view.findViewById(R.id.username);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dob);
        totalFriendsTV = view.findViewById(R.id.total_friends);
        actionBtn = view.findViewById(R.id.button_action);
        deleteBtn = view.findViewById(R.id.button_delete);
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);


        dataUtils = new DataUtils();
        currentUserId = FirebaseAuth.getInstance().getUid();

        // Handle back button click
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Get the user data passed through arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("user")) {
            user = (User) args.getSerializable("user");
            displayUserData();
            setupUIBasedOnRole();
        } else {
            Toast.makeText(requireContext(), "No user data provided", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    /**
     * Displays the user data
     */
    private void displayUserData() {
        if (user != null) {
            // Load the avatar image
            Glide.with(requireContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.sample)
                    .circleCrop()
                    .into(avatar);

            // Set the text fields
            username.setText(user.getUsername());
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            gender.setText(user.getGender().toString().toLowerCase());
            dob.setText(user.getDoB());
            // Count and display total friends
            dataUtils.countTotalFriends(user.getId(), new DataUtils.FetchCallback<Integer>() {
                @Override
                public void onSuccess(Integer totalFriends) {
                    // Log the total friends count
                    Log.d("UserProfileFragment", "Total friends for user " + user.getUsername() + " (" + user.getId() + "): " + totalFriends);
                    totalFriendsTV.setText(String.valueOf(totalFriends));
                }

                @Override
                public void onFailure(Exception e) {
                    totalFriendsTV.setText("0");
                    Toast.makeText(requireContext(), "Failed to count friends", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Sets up the UI based on whether the current user is an Admin or not.
     */
    private void setupUIBasedOnRole() {
        boolean isAdmin = UserSession.getInstance().getCurrentUser().getAccountType() == UserType.ADMIN;

        if (isAdmin) {
            actionBtn.setVisibility(View.GONE); // Hide the Add Friend button
            deleteBtn.setVisibility(View.VISIBLE); // Show the Delete button
            friendsRecyclerView.setVisibility(View.VISIBLE); // Show the friends list

            setupDeleteButton();
            loadFriendsList();
        } else {
            deleteBtn.setVisibility(View.GONE); // Hide the Delete button
            friendsRecyclerView.setVisibility(View.GONE); // Hide the friends list
            checkFriendshipStatus(); // Check friendship status for regular users
        }
    }

    /**
     * Sets up the Delete button for Admins.
     */
    private void setupDeleteButton() {
        deleteBtn.setOnClickListener(v -> {
            dataUtils.deleteById("users", user.getId(), new DataUtils.NormalCallback<Void>() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Loads the list of friends for the displayed user.
     */
    private void loadFriendsList() {
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<User> friendList = new ArrayList<>();
        FriendList adapter = new FriendList(requireContext(), friendList);
        friendsRecyclerView.setAdapter(adapter);

        dataUtils.fetchFriendsForUser(user.getId(), new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> friends) {
                friendList.clear();
                friendList.addAll(friends);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load friends list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks the friendship status between the current user and the displayed user.
     * Updates the button accordingly.
     */
    private void checkFriendshipStatus() {
        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                for (Relationship relationship : relationships) {
                    if ((relationship.getFirstUser().equals(currentUserId) && relationship.getSecondUser().equals(user.getId())) ||
                            (relationship.getSecondUser().equals(currentUserId) && relationship.getFirstUser().equals(user.getId()))) {

                        if (relationship.getStatus() == FriendStatus.FRIEND) {
                            actionBtn.setText("Friends");
                            actionBtn.setEnabled(false);
                        } else if (relationship.getStatus() == FriendStatus.PENDING && relationship.getSecondUser().equals(currentUserId)) {
                            actionBtn.setText("Accept");
                            actionBtn.setEnabled(true);
                            actionBtn.setOnClickListener(v -> acceptFriendRequest(relationship));
                        } else if (relationship.getStatus() == FriendStatus.PENDING) {
                            actionBtn.setText("Pending");
                            actionBtn.setEnabled(false);
                        }
                        return;
                    }
                }

                // Default: Not a friend
                actionBtn.setText("Add Friend");
                actionBtn.setEnabled(true);
                actionBtn.setOnClickListener(v -> sendFriendRequest());
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to fetch relationships", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sends a friend request to the displayed user.
     */
    private void sendFriendRequest() {
        Relationship newRelationship = new Relationship(
                null,
                currentUserId,
                user.getId(),
                Utils.getCurrentDate(),
                null,
                FriendStatus.PENDING,
                0
        );

        dataUtils.addRelationship(newRelationship, new DataUtils.NormalCallback<Void>() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
                actionBtn.setText("Pending");
                actionBtn.setEnabled(false);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to send friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Accepts a friend request from the displayed user.
     */
    private void acceptFriendRequest(Relationship relationship) {
        relationship.setStatus(FriendStatus.FRIEND);
        relationship.setDateCreated(Utils.getCurrentDate());

        dataUtils.updateRelationship(relationship, new DataUtils.NormalCallback<Void>() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Friend request accepted!", Toast.LENGTH_SHORT).show();
                actionBtn.setText("Friends");
                actionBtn.setEnabled(false);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to accept friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
