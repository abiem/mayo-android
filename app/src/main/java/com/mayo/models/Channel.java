package com.mayo.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class Channel {
    private UserId userId;
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

}
