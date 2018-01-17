package com.mayo.models;

import java.util.ArrayList;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class Users {
    private String UpdatedAt;
    private String deviceToken;
    private boolean isDemoTaskShown;
    private int score;
    private ArrayList<Location> location;
    private ArrayList<ScoreDetail> scoreDetail;
    private TaskCreated taskCreated;
    private TaskParticipated taskParticipated;

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isDemoTaskShown() {
        return isDemoTaskShown;
    }

    public void setDemoTaskShown(boolean demoTaskShown) {
        isDemoTaskShown = demoTaskShown;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Location> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Location> location) {
        this.location = location;
    }
}
