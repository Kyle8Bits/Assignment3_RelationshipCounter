package com.example.assignment3_relationshipcounter.service.models;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    public static Gender fromString(String gender) {
        switch (gender.toLowerCase()) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            case "other":
                return OTHER;
            default:
                return null;
        }
    }

    public static String toString(Gender gender) {
        switch (gender) {
            case MALE:
                return "Male";
            case FEMALE:
                return "Female";
            case OTHER:
                return "Other";
            default:
                return null;
        }
    }

}
