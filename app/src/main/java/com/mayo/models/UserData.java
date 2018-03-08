package com.mayo.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class UserData {
    private String UpdatedAt;
    private String deviceToken;
    private boolean isDemoTaskShown;
    private int score;
    private HashMap location;
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

    public HashMap getLocation() {
        return location;
    }

    public void setLocation(HashMap location) {
        this.location = location;
    }

    public ArrayList<ScoreDetail> getScoreDetail() {
        return scoreDetail;
    }

    public void setScoreDetail(ArrayList<ScoreDetail> scoreDetail) {
        this.scoreDetail = scoreDetail;
    }

    public TaskCreated getTaskCreated() {
        return taskCreated;
    }

    public void setTaskCreated(TaskCreated taskCreated) {
        this.taskCreated = taskCreated;
    }

    public TaskParticipated getTaskParticipated() {
        return taskParticipated;
    }

    public void setTaskParticipated(TaskParticipated taskParticipated) {
        this.taskParticipated = taskParticipated;
    }
}
