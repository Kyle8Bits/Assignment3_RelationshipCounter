package com.example.assignment3_relationshipcounter.service.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event implements Serializable {
    private String id;
    private String title;
    private String description;
    private String date;
    private String relationshipId;
    private String status;

    public Event() {

    }

    public Event(String title, String description, String date, String relationshipId) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.relationshipId = relationshipId;
        this.status = calculateStatus(date);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String calculateStatus(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = sdf.parse(dateString);
            Date currentDate = new Date();

            if (eventDate.before(currentDate)) {
                return "Ended";
            } else if (sdf.format(eventDate).equals(sdf.format(currentDate))) {
                return "Happening";
            } else {
                return "Upcoming";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "unknown";
        }
    }
}
