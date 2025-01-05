package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.ChatRoom;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRoomList extends FirestoreRecyclerAdapter<ChatRoom, ChatRoomList.ChatRoomViewHolder> {
    DataUtils dataUtils = new DataUtils();
    Context context;
    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    public ChatRoomList(@NonNull FirestoreRecyclerOptions<ChatRoom> options, Context context, OnItemClickListener listener) {
        super(options);
        this.context = context;
        this.onItemClickListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position, @NonNull ChatRoom model) {
        dataUtils.getAllChatRoomOfUser(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(new Authentication().getFUser().getUid());

                        User user = task.getResult().toObject(User.class);
                        holder.usernameText.setText(user.getUsername());
                        if(lastMessageSentByMe){
                            holder.lastMessageText.setText("You: " + model.getLastMessage());
                        }
                        else {
                            holder.lastMessageText.setText(model.getLastMessage());
                        }
                        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
                        holder.lastTimeText.setVisibility(View.VISIBLE);
                        holder.lastTimeText.setText(dataUtils.timestampToString(model.getLastMessageTime()));
                    } else {
                        System.out.println("Error getting document: " + task.getException());
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_friend_adapter, parent, false);
        return new ChatRoomViewHolder(view);
    }

    class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastTimeText;
        ImageView profilePic;
        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.card_friendname);
            lastMessageText = itemView.findViewById(R.id.display_param);
            lastTimeText = itemView.findViewById(R.id.last_time_message);
        }
    }
}
