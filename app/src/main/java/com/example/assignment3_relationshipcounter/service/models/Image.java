package com.example.assignment3_relationshipcounter.service.models;

import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;

public class Image implements DataUtils.HasId {

    private String id;
    private String owner;

    private String base64Value;
    private String dateAdd;
    private String timeAdd;

    public Image(String id, String owner, String base64Value, String dateAdd, String timeAdd) {
        this.id = id;
        this.owner = owner;
        this.base64Value = base64Value;
        this.dateAdd = dateAdd;
        this.timeAdd = timeAdd;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBase64Value() {
        return base64Value;
    }

    public void setBase64Value(String base64Value) {
        this.base64Value = base64Value;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getTimeAdd() {
        return timeAdd;
    }

    public void setTimeAdd(String timeAdd) {
        this.timeAdd = timeAdd;
    }
}
