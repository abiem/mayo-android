package com.mayo.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Lakshmikodali on 08/01/18.
 */

public class TutorialModel {
    private String textMessage;
    private String buttonMessage;
    private Drawable backgroundView;

    public Drawable getBackgroundView() {
        return backgroundView;
    }

    public void setBackgroundView(Drawable backgroundView) {
        this.backgroundView = backgroundView;
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

}
