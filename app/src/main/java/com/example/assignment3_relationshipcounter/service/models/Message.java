package com.example.assignment3_relationshipcounter.service.models;

public class Message {
    private String message;
    private String senderId;
    private String time;
    private String date;

    public Message(String message, String senderId, String time, String date) {
        this.message = message;
        this.senderId = senderId;
        this.time = time;
        this.date = date;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}