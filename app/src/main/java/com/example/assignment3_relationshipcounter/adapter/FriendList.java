package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;

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
            @Override
            public void onSuccess(Relationship relationship) {
                String currentUserId = UserSession.getInstance().getCurrentUser().getId();

                switch (relationship.getStatus()) {
                    case FRIEND:
                        // User is already a friend
                        holder.addFriendButton.setVisibility(View.GONE);
                        holder.navigateIcon.setVisibility(View.VISIBLE);
                        break;
                    case PENDING:
                        // Friend request is pending
                        holder.addFriendButton.setVisibility(View.VISIBLE);
                        holder.navigateIcon.setVisibility(View.GONE);

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
        // Compare the old and new list sizes to determine specific changes
        int oldSize = userList.size();
        int newSize = newList.size();

        Log.d("FriendList", "Updating adapter with " + newList.size() + " users.");

        // Remove items that are no longer in the new list
        for (int i = oldSize - 1; i >= 0; i--) {
            if (!newList.contains(userList.get(i))) {
                userList.remove(i);
                notifyItemRemoved(i);
            }
        }

        // Add new items or update existing ones
        for (int i = 0; i < newSize; i++) {
            if (i >= oldSize || !userList.get(i).equals(newList.get(i))) {
                if (i >= oldSize) {
                    userList.add(newList.get(i)); // Add new item
                    notifyItemInserted(i);
                } else {
                    userList.set(i, newList.get(i)); // Update existing item

                    Log.d("FriendList", "Adapter dataset updated. Total items: " + userList.size());
                    notifyItemChanged(i);
                }
            }
        }
    }
}

// ViewHolder
class FriendListView extends RecyclerView.ViewHolder {

    TextView friendName;
    Button addFriendButton;
    FrameLayout navigateIcon;

    public FriendListView(@NonNull View itemView) {
        super(itemView);

        friendName = itemView.findViewById(R.id.card_friendname);
        addFriendButton = itemView.findViewById(R.id.btn_add_friend);
        navigateIcon = itemView.findViewById(R.id.card_navigate);

    }
}


