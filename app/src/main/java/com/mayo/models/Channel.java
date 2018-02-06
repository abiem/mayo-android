package com.mayo.models;

import java.util.ArrayList;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class Channel {
    private ArrayList<UserId> users;
    private ArrayList<Message> messages;

    public ArrayList<UserId> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserId> users) {
        this.users = users;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
