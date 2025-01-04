package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

/**
 * This adapter is from FireStore it can update the UI realtime without reloading data
 * This might different with original adapter, please do not delete, it core part for messaging
 */
public class ChatMessageList extends FirestoreRecyclerAdapter<Message, ChatMessageList.ChatModelViewHolder> {

    Context context;
    String currentUserId;
    String otherUserId;

    public ChatMessageList(@NonNull FirestoreRecyclerOptions<Message> options, Context context, String currentUserId, String otherUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull Message model) {
        if(model.getSenderId().equals(currentUserId)){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_bubble_fragment,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_tv);
            rightChatTextview = itemView.findViewById(R.id.right_chat_tv);
        }
    }
}
