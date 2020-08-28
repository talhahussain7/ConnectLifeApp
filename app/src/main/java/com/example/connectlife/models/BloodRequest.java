package com.example.connectlife.models;

public class BloodRequest {
    User user;
    String reqStatus;

    public BloodRequest(User user, String reqStatus) {
        this.user = user;
        this.reqStatus = reqStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }
}
