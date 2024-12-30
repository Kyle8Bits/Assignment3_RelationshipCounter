package com.example.assignment3_relationshipcounter.service.models;

import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;

//normal user
public class User implements DataUtils.HasId {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String DoB;
    private Gender gender;
    private String phoneNumber;
    private UserType accountType;

    public User() {
    }

    public User(String id, String firstName, String lastName, String email, String password, String doB, Gender gender, String phoneNumber, UserType accountType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        DoB = doB;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
