package com.example.assignment3_relationshipcounter.service.models;


import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.google.firebase.Timestamp;

//separate the relationship to maintain the code clean
public class Relationship implements DataUtils.HasId {
    private String id;
    private String firstUser;
    private String secondUser;
    private Timestamp dateCreated;
    private Timestamp dateAccept;
    private FriendStatus status;
    private int counter;

    public Relationship() {}

    public Relationship(String id, String firstUser, String secondUser, Timestamp dateCreated, Timestamp dateAccept, FriendStatus status, int counter) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.dateCreated = dateCreated;
        this.dateAccept = dateAccept;
        this.status = status;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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

    public String getDateCreated() {
        return dateCreated.toDate().toString();
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateAccept() {
        return dateAccept.toDate().toString();
    }

    public void setDateAccept(Timestamp dateAccept) {
        this.dateAccept = dateAccept;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
