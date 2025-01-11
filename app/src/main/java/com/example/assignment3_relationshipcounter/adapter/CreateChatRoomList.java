package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CreateChatRoomList extends RecyclerView.Adapter<CreateChatRoomList.UserViewHolder> {

    private List<User> userList = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public CreateChatRoomList(Context context, OnItemClickListener listener) {
        this.context = context;
        this.onItemClickListener = listener;
    }

    public void updateUserList(List<User> users) {
        this.userList.clear();
        this.userList.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_friend_adapter, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameText.setText(user.getUsername());

        // Fetch and display the day count and avatar
        String currentUserId = FirebaseAuth.getInstance().getUid();

        // Fetch relationship for the user
        DataUtils dataUtils = new DataUtils();
        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                boolean isFriend = false;
                for (Relationship relationship : relationships) {
                    if ((relationship.getFirstUser().equals(currentUserId) && relationship.getSecondUser().equals(user.getId())) ||
                            (relationship.getSecondUser().equals(currentUserId) && relationship.getFirstUser().equals(user.getId()))) {
                        if (relationship.getStatus() == FriendStatus.FRIEND) {
                            // Calculate and display the day count
                            long dayCount = Utils.calculateDayCount(relationship.getDateCreated());
                            holder.dayCount.setText(dayCount + " days");
                            isFriend = true;
                            break;
                        }
                    }
                }

                // If no friendship found, clear the day count
                if (!isFriend) {
                    holder.dayCount.setText("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                holder.dayCount.setText("");
            }
        });

        // Load the user avatar
        Glide.with(context)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.sample)
                .centerCrop()
                .into(holder.avatar);

        // Handle item click
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageView avatar;
        TextView dayCount;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.card_friendname);
            avatar = itemView.findViewById(R.id.avatar);
            dayCount = itemView.findViewById(R.id.day_count);
        }
    }
}

