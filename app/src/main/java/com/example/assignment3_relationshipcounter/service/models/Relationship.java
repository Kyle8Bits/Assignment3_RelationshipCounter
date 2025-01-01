package com.example.assignment3_relationshipcounter.service.models;


import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;

//separate the relationship to maintain the code clean
public class Relationship implements DataUtils.HasId {
    private String id;
    private String firstUser;
    private String secondUser;
    private String dateCreated;
    private String dateAccept;
    private FriendStatus status;
    private int counter;

    public Relationship() {}

    public Relationship(String id, String firstUser, String secondUser, String dateCreated, String dateAccept, FriendStatus status, int counter) {
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
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateAccept() {
        return dateAccept;
    }

    public void setDateAccept(String dateAccept) {
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
