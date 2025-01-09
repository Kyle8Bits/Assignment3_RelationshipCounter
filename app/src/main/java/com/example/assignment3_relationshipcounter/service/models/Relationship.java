package com.example.assignment3_relationshipcounter.service.models;

import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.google.firebase.Timestamp;

// Separate the relationship to maintain code clarity
public class Relationship implements DataUtils.HasId {
    private String id;
    private String firstUser;
    private String secondUser;
    private Timestamp dateCreated;
    private FriendStatus status;
    private int counter;

    public Relationship() {}

    public Relationship(String id, String firstUser, String secondUser, Timestamp dateCreated, Timestamp dateAccept, FriendStatus status, int counter) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.dateCreated = dateCreated;

        this.status = status;
        this.counter = counter;
    }

    // Unique ID
    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // User Details
    public String getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(String firstUser) {
        this.firstUser = firstUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(String secondUser) {
        this.secondUser = secondUser;
    }

    public Timestamp getDateCreated() {
        return dateCreated; // Return the raw Timestamp
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    // Relationship Status
    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    // Counter (e.g., number of interactions, days as friends)
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void updateStatus(FriendStatus newStatus) {
        this.status = newStatus;
    }

    // Utility Methods for Relationship Status
    public boolean isFriend() {
        return status == FriendStatus.FRIEND;
    }

    public boolean isPending() {
        return status == FriendStatus.PENDING;
    }

    public boolean isBlocked() {
        return status == FriendStatus.BLOCKED;
    }

    public boolean isNotFriend() {
        return status == FriendStatus.NOT_FRIEND;
    }

    // Relationship Display Text
    public String getStatusDisplayText() {
        switch (status) {
            case FRIEND:
                return "Friend";
            case PENDING:
                return "Pending Request";
            case BLOCKED:
                return "Blocked";
            case NOT_FRIEND:
                return "Not Friend";
            default:
                return "Unknown";
        }
    }

    // Relationship Validation (Optional)
    public boolean isValid() {
        return firstUser != null && secondUser != null && status != null;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "id='" + id + '\'' +
                ", firstUser='" + firstUser + '\'' +
                ", secondUser='" + secondUser + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", status=" + status +
                ", counter=" + counter +
                '}';
    }
}
