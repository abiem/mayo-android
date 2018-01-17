package com.mayo.models;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class ScoreDetail {
    private String createdDate;
    private int points;
    private String taskID;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
}
