package com.example.assignment3_relationshipcounter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.models.GalleryItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final List<GalleryItem> galleryItemList;

    public GalleryAdapter(List<GalleryItem> galleryItemList) {
        this.galleryItemList = galleryItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_image, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryItem item = galleryItemList.get(position);

        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.imageView);
        holder.descriptionTextView.setText("Title: " + item.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.dateTextView.setText("Date added: " + dateFormat.format(item.getDateAdded().toDate()));
    }

    @Override
    public int getItemCount() {
        return galleryItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image);
            descriptionTextView = itemView.findViewById(R.id.description_text);
            dateTextView = itemView.findViewById(R.id.date_text);
        }
    }
}
