package com.example.assignment3_relationshipcounter.service.models;

import com.google.firebase.Timestamp;

public class Message {
    private String message;
    private String senderId;
    private Timestamp lastMessageTime;

    public Message(){

    }

    public Message(String message, String senderId, Timestamp lastMessageTime) {
        this.message = message;
        this.senderId = senderId;
        this.lastMessageTime = lastMessageTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getDate(){
        return lastMessageTime.toDate().toString();
    }

    public String getTime(){
        return lastMessageTime.toDate().toString();
    }
}