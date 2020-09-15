package com.example.connectlife.models;

public class History {




String requestor;
String requestorId;
String acceptedBy;
String acceptedById;
String bloodGroup;
String otherNum;
String status;
String location;
String date;

    public History(String requestor, String requestorId, String acceptedBy, String acceptedById, String bloodGroup, String otherNum, String status, String location, String date) {
        this.requestor = requestor;
        this.requestorId = requestorId;
        this.acceptedBy = acceptedBy;
        this.acceptedById = acceptedById;
        this.bloodGroup = bloodGroup;
        this.otherNum = otherNum;
        this.status = status;
        this.location = location;
        this.date = date;
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public String getAcceptedById() {
        return acceptedById;
    }

    public void setAcceptedById(String acceptedById) {
        this.acceptedById = acceptedById;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getOtherNum() {
        return otherNum;
    }

    public void setOtherNum(String otherNum) {
        this.otherNum = otherNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
