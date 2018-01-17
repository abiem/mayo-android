package com.mayo.models;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class Message {
    private String colorIndex;
    private String dateCreated;
    private String senderId;
    private String senderName;
    private String text;

    public String getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(String colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
