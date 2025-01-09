package com.example.assignment3_relationshipcounter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.fragments.FriendshipDetailFragment;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends RecyclerView.Adapter<FriendListView> {

    private final Context context;
    private List<User> userList;

    public FriendList(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public FriendListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_friend_adapter, parent, false);
        return new FriendListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListView holder, int position) {
        User user = userList.get(position);

        // Set Friend Name
        holder.friendName.setText(user.getUsername());

        // Fetch relationship for the user
        fetchRelationshipForUser(user, new DataUtils.FetchCallback<Relationship>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Relationship relationship) {
                String currentUserId = UserSession.getInstance().getCurrentUser().getId();

                switch (relationship.getStatus()) {
                    case FRIEND:
                        // User is already a friend
                        holder.addFriendButton.setVisibility(View.GONE);
                        holder.navigateIcon.setVisibility(View.VISIBLE);

                        // Calculate and display the Day Count
                        long dayCount = Utils.calculateDayCount(relationship.getDateCreated());
                        holder.dayCount.setText(dayCount + " days"); // Bind day count
                        holder.dayCount.setVisibility(View.VISIBLE);

                        // Set click listener to navigate to Friendship Detail
                        holder.itemView.setOnClickListener(v -> {
                            navigateToFriendshipDetail(user, relationship);
                        });
                        break;
                    case PENDING:
                        // Friend request is pending
                        holder.addFriendButton.setVisibility(View.VISIBLE);
                        holder.navigateIcon.setVisibility(View.GONE);
                        holder.dayCount.setVisibility(View.GONE);
                        // Remove click listener since it's not a friend
                        holder.itemView.setOnClickListener(null);

                        if (relationship.getFirstUser().equals(currentUserId)) {
                            // Current user is the sender
                            holder.addFriendButton.setText("Pending");
                            holder.addFriendButton.setEnabled(false);
                        } else if (relationship.getSecondUser().equals(currentUserId)) {
                            // Current user is the receiver
                            holder.addFriendButton.setText("Accept");
                            holder.addFriendButton.setEnabled(true);

                            // Handle "Accept" Button Click
                            holder.addFriendButton.setOnClickListener(v -> {
                                // Log the relationship details
                                Log.d("AcceptButtonClick", "Accepting Friend Request: " + relationship.toString());

                                // Update the status to "Friend" and set the created date
                                relationship.setStatus(FriendStatus.FRIEND);
                                relationship.setDateCreated(new Utils().getCurrentDate());

                                new DataUtils().updateRelationship(relationship, new DataUtils.NormalCallback<Void>() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(context, "You are now friends with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                        holder.addFriendButton.setVisibility(View.GONE);
                                        holder.navigateIcon.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(context, "Failed to accept friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }

                        break;
                    case NOT_FRIEND:
                        // User is not a friend
                        holder.addFriendButton.setVisibility(View.VISIBLE);
                        holder.addFriendButton.setText("Add Friend");
                        holder.addFriendButton.setEnabled(true);
                        holder.navigateIcon.setVisibility(View.GONE);
                        holder.dayCount.setVisibility(View.GONE);
                        // Remove click listener since it's not a friend
                        holder.itemView.setOnClickListener(null);

                        // Handle "Add Friend" Button Click
                        holder.addFriendButton.setOnClickListener(v -> {
                            holder.addFriendButton.setText("Pending");
                            holder.addFriendButton.setEnabled(false);

                            // Create a new relationship with "Pending" status
                            Relationship newRelationship = new Relationship(
                                    null, // ID will be auto-generated
                                    currentUserId,
                                    user.getId(),
                                    new Utils().getCurrentDate(), // Date created
                                    null, // Date accepted (null for now)
                                    FriendStatus.PENDING,
                                    0
                            );

                            new DataUtils().addRelationship(newRelationship, new DataUtils.NormalCallback<Void>() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Friend request sent to " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    holder.addFriendButton.setText("Add Friend");
                                    holder.addFriendButton.setEnabled(true);
                                    Toast.makeText(context, "Failed to send friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                        break;
                    default:
                        // Default case: NOT_FRIEND
                        holder.addFriendButton.setVisibility(View.VISIBLE);
                        holder.addFriendButton.setText("Add Friend");
                        holder.addFriendButton.setEnabled(true);
                        holder.navigateIcon.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onFailure(Exception e) {
                holder.addFriendButton.setVisibility(View.VISIBLE);
                holder.addFriendButton.setText("Add Friend");
                holder.addFriendButton.setEnabled(true);
                holder.navigateIcon.setVisibility(View.GONE);
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


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        Log.d("FriendList", "Updating adapter with " + newList.size() + " users.");

        // Use DiffUtil to calculate the changes
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return userList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // Compare unique IDs to determine if items represent the same user
                return userList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // Compare entire objects to determine if their content has changed
                return userList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        });

        // Merge new data with the existing list without clearing
        mergeLists(newList);

        Log.d("FriendList", "After Merge: Updating adapter with " + newList.size() + " users.");

        // Notify the adapter of changes
        diffResult.dispatchUpdatesTo(this);
    }

    // Helper function to merge lists
    private void mergeLists(List<User> newList) {
        Log.d("FriendList", "Merging new list with the existing data.");
        for (User newUser : newList) {
            boolean exists = false;
            for (User existingUser : userList) {
                if (existingUser.getId().equals(newUser.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                userList.add(newUser);
            }
        }

        // Remove users not in the new list
        List<User> toRemove = new ArrayList<>();
        for (User existingUser : userList) {
            boolean stillExists = false;
            for (User newUser : newList) {
                if (existingUser.getId().equals(newUser.getId())) {
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists) {
                toRemove.add(existingUser);
            }
        }

        userList.removeAll(toRemove);
        Log.d("FriendList", "Merge complete. Total users: " + userList.size());
    }

    private void navigateToFriendshipDetail(User friend, Relationship relationship) {
        // Create instance of FriendshipDetailFragment
        FriendshipDetailFragment fragment = new FriendshipDetailFragment();

        // Pass user and relationship details using Bundle
        Bundle args = new Bundle();
        args.putString("friendId", friend.getId());
        args.putString("friendName", friend.getUsername());
        args.putLong("daysTogether", Utils.calculateDayCount(relationship.getDateCreated())); // Pass day count
        args.putString("relationshipId", relationship.getId()); // Pass relationship ID if needed
        fragment.setArguments(args);

        // Perform navigation
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Replace with your container ID
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("FriendList", "Context is not an instance of AppCompatActivity");
        }
    }


}

// ViewHolder
class FriendListView extends RecyclerView.ViewHolder {

    TextView friendName, dayCount;
    Button addFriendButton;
    FrameLayout navigateIcon;

    public FriendListView(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        friendName = itemView.findViewById(R.id.card_friendname);
        dayCount = itemView.findViewById(R.id.day_count);
        addFriendButton = itemView.findViewById(R.id.btn_add_friend);
        navigateIcon = itemView.findViewById(R.id.card_navigate);

    }
}


