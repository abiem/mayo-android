package com.mayo.models;

/**
 * Created by Lakshmi on 21/02/18.
 */

public class TaskLatLng {
    private Task task;
    private TaskLocations taskLocations;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskLocations getTaskLocations() {
        return taskLocations;
    }

    public void setTaskLocations(TaskLocations taskLocations) {
        this.taskLocations = taskLocations;
    }
}
