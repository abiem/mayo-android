package com.mayo.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class TaskCreated {
    private int count;
    private HashMap<String,TaskId> tasks;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, TaskId> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, TaskId> tasks) {
        this.tasks = tasks;
    }
}
