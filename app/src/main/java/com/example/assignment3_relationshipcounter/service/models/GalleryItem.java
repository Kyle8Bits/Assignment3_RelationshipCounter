package com.example.assignment3_relationshipcounter.service.models;

import com.google.firebase.Timestamp;

public class GalleryItem {
    private String id;
    private String relationshipId;
    private String imageUrl;
    private String description;
    private Timestamp dateAdded;

    // Required no-argument constructor
    public GalleryItem() {}

    public GalleryItem(String id, String relationshipId, String imageUrl, String description, Timestamp dateAdded) {
        this.id = id;
        this.relationshipId = relationshipId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.dateAdded = dateAdded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }
}
