package com.example.assignment3_relationshipcounter.service.models;

import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;

import java.io.Serializable;

//normal user
public class User implements DataUtils.HasId, Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String DoB;
    private Gender gender;
    private String phoneNumber;
    private UserType accountType;
    private double latitude;
    private double longitude;
    private String avatarUrl;
    public User() {
    }

    public User(String id, String firstName, String lastName, String username, String email, String doB, Gender gender, String phoneNumber, UserType accountType, double latitude, double longitude) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        DoB = doB;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avatarUrl = "";
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoB() {
        return DoB;
    }

    public void setDoB(String doB) {
        DoB = doB;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getAccountType() {
        return accountType;
    }

    public void setAccountType(UserType accountType) {
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
