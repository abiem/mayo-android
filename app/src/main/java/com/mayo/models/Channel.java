package com.mayo.models;

import java.util.ArrayList;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class Channel {
    private String channelId;
    private ArrayList<UserId> users;
    private ArrayList<Message> messages;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

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
