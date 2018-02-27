package com.mayo.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class Task implements Serializable{
    private String taskDescription;
    private boolean completed;
    private String timeCreated;
    private String timeUpdated;
    private String startColor;
    private String endColor;
    private String taskID;
    private String completeType;
    private String helpedBy;
    private String createdby;
    private boolean recentActivity;
    private boolean userMovedOutside;

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public String getStartColor() {
        return startColor;
    }

    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getCompleteType() {
        return completeType;
    }

    public void setCompleteType(String completeType) {
        this.completeType = completeType;
    }

    public String getHelpedBy() {
        return helpedBy;
    }

    public void setHelpedBy(String helpedBy) {
        this.helpedBy = helpedBy;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public boolean isRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(boolean recentActivity) {
        this.recentActivity = recentActivity;
    }

    public boolean isUserMovedOutside() {
        return userMovedOutside;
    }

    public void setUserMovedOutside(boolean userMovedOutside) {
        this.userMovedOutside = userMovedOutside;
    }
}
