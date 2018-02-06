package com.mayo.models;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 06/02/18.
 */

public class TaskViews {
    private int count;
    private ArrayList<UserId> userIds;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<UserId> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<UserId> userIds) {
        this.userIds = userIds;
    }
}
