package com.mayo.models;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MapDataModel {
    private Drawable backgroundView;
    private GradientColor gradientColor;
    private String textMessage;
    private String buttonMessage;
    private int fakeCardPosition;
    private boolean isFakeCard;
    private CardView cardView;
    private CardLatlng cardLatlng;

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
}
