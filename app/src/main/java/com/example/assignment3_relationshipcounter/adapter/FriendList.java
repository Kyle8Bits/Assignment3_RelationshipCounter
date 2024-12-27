package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;

public class FriendList extends RecyclerView.Adapter<FriendListView>{

    Context context;

    public FriendList(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public FriendListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendListView(LayoutInflater.from(context).inflate(R.layout.card_friend_relation_number, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}

class FriendListView extends RecyclerView.ViewHolder{

    public FriendListView(@NonNull View itemView) {
        super(itemView);

    }
}