package com.mayo.models;

import java.util.ArrayList;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class TaskCreated {
    int count;
    ArrayList<TaskId> tasks;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<TaskId> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskId> tasks) {
        this.tasks = tasks;
    }
}
