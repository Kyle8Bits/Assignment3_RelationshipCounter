package com.example.assignment3_relationshipcounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.assignment3_relationshipcounter.R;

public class StoryList extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_FIRST_ITEM = 0;
    private static final int TYPE_OTHER_ITEMS = 1;
    Context context;

    public StoryList(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        // Return different view types based on the position
        return position == 0 ? TYPE_FIRST_ITEM : TYPE_OTHER_ITEMS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FIRST_ITEM) {
            // Inflate layout for the first item
            View view = LayoutInflater.from(context).inflate(R.layout.card_my_story, parent, false);
            return new MyStory(view); // Return the correct ViewHolder
        } else {
            // Inflate layout for other items
            View view = LayoutInflater.from(context).inflate(R.layout.card_friend_story, parent, false);
            return new StoryListView(view); // Return the correct ViewHolder
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_FIRST_ITEM) {
            // Bind data for the first item
            MyStory firstHolder = (MyStory) holder;
            // Set up the first item's data here
        } else {
            // Bind data for other items
            StoryListView otherHolder = (StoryListView) holder;
            // Set up other items' data here
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}

class StoryListView extends RecyclerView.ViewHolder{

    public StoryListView(@NonNull View itemView) {
        super(itemView);

    }
}

class MyStory extends RecyclerView.ViewHolder{
    public MyStory(@NonNull View itemView) {
        super(itemView);
    }

}