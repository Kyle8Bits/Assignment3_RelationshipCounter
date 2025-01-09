package com.example.assignment3_relationshipcounter.utils;

import com.example.assignment3_relationshipcounter.service.models.User;

public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
