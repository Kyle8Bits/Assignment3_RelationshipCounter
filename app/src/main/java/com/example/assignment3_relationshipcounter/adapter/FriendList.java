package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.models.User;

import java.util.List;

public class FriendList extends RecyclerView.Adapter<FriendListView> {

    private final Context context;
    private List<User> userList;

    // Constructor to initialize the adapter with context and user list
    public FriendList(Context context) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public FriendListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_friend_relation_number, parent, false);
        return new FriendListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListView holder, int position) {
        // Get the user at the current position
        User user = userList.get(position);

        // Set user data to views
        holder.friendName.setText(user.getUsername()); // Display username
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Method to update the user list dynamically
    public void updateList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }
}

// ViewHolder for the FriendList adapter
class FriendListView extends RecyclerView.ViewHolder {

    TextView friendName;

    public FriendListView(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        friendName = itemView.findViewById(R.id.tv_friend_name);
    }
}
