package com.mayo.models;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;

import java.io.Serializable;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MapDataModel implements Serializable {
    private transient Drawable backgroundView;
    private GradientColor gradientColor;
    private String textMessage;
    private String buttonMessage;
    private int fakeCardPosition;
    private boolean isFakeCard;
    private transient CardView cardView;
    private transient CardLatlng cardLatlng;
    private boolean completed;
    private String timeCreated;
    private TaskLatLng taskLatLng;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isFakeCard() {
        return isFakeCard;
    }

    public void setFakeCard(boolean fakeCard) {
        isFakeCard = fakeCard;
    }

    public Drawable getBackgroundView() {
        return backgroundView;
    }

    public void setBackgroundView(Drawable backgroundView) {
        this.backgroundView = backgroundView;
    }

    public GradientColor getGradientColor() {
        return gradientColor;
    }

    public void setGradientColor(GradientColor gradientColor) {
        this.gradientColor = gradientColor;
    }

    public int getFakeCardPosition() {
        return fakeCardPosition;
    }

    public void setFakeCardPosition(int fakeCardPosition) {
        this.fakeCardPosition = fakeCardPosition;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getButtonMessage() {
        return buttonMessage;
    }

    public void setButtonMessage(String buttonMessage) {
        this.buttonMessage = buttonMessage;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public CardLatlng getCardLatlng() {
        return cardLatlng;
    }

    public void setCardLatlng(CardLatlng cardLatlng) {
        this.cardLatlng = cardLatlng;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public TaskLatLng getTaskLatLng() {
        return taskLatLng;
    }

    public void setTaskLatLng(TaskLatLng taskLatLng) {
        this.taskLatLng = taskLatLng;
    }
}
