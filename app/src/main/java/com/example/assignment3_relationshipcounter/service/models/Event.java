package com.example.assignment3_relationshipcounter.service.models;

import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    private String title;
    private String description;
    private String date;
    private String relationshipId;

    public Event() {

    }

    public Event(String title, String description, String date, String relationshipId) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.relationshipId = relationshipId;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
