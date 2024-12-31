package com.example.assignment3_relationshipcounter.service.models;

public enum FriendStatus {
    FRIEND,
    PENDING,
    BLOCKED,
    NOT_FRIEND;

    public static FriendStatus fromString(String status) {
        switch (status.toLowerCase()) {
            case "friend":
                return FRIEND;
            case "pending":
                return PENDING;
            case "blocked":
                return BLOCKED;
            case "add friend":
                return NOT_FRIEND;
            default:
                return null;
        }
    }
    public static String toString(FriendStatus status) {
        switch (status) {
            case FRIEND:
                return "Friend";
            case PENDING:
                return "Pending";
            case BLOCKED:
                return "Blocked";
            case NOT_FRIEND:
                return "Add friend";
            default:
                return null;
        }
    }
}

