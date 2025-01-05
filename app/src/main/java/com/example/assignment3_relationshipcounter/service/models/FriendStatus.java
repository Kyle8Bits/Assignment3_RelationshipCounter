package com.example.assignment3_relationshipcounter.service.models;

import java.util.HashMap;
import java.util.Map;

public enum FriendStatus {
    FRIEND("Friend"),
    PENDING("Pending"),
    BLOCKED("Blocked"),
    NOT_FRIEND("Add friend");

    private final String status;
    private static final Map<String, FriendStatus> STATUS_MAP = new HashMap<>();

    // Populate the map for reverse lookup
    static {
        for (FriendStatus friendStatus : values()) {
            STATUS_MAP.put(friendStatus.status.toLowerCase(), friendStatus);
        }
    }

    FriendStatus(String status) {
        this.status = status;
    }

    public static FriendStatus fromString(String status) {
        return STATUS_MAP.getOrDefault(status.toLowerCase(), null);
    }

    @Override
    public String toString() {
        return status;
    }
}
