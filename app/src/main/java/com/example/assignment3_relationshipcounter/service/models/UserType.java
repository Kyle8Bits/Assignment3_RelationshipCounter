package com.example.assignment3_relationshipcounter.service.models;

public enum UserType {
    ADMIN,
    USER;

    public static UserType fromString(String type) {
        switch (type.toLowerCase()) {
            case "admin":
                return ADMIN;
            case "user":
                return USER;
            default:
                return null;
        }
    }

    public static String toString(UserType type) {
        switch (type) {
            case ADMIN:
                return "Admin";
            case USER:
                return "User";
            default:
                return null;
        }
    }

}
