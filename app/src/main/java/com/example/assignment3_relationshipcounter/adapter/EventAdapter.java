package com.example.assignment3_relationshipcounter.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    public void updateEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.status.setText(event.getStatus());
        if (event.getStatus().equalsIgnoreCase("ended")) {
            holder.status.setBackgroundResource(R.drawable.text_end);
            holder.status.setTextColor(Color.parseColor("#A594F9"));
        } else {
            holder.status.setBackgroundResource(R.drawable.text_upcoming);
            holder.status.setTextColor(Color.parseColor("#8ABFA3"));
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, status;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            description = itemView.findViewById(R.id.event_description);
            status = itemView.findViewById(R.id.status);
        }
    }
}

