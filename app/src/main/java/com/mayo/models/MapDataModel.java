package com.mayo.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MapDataModel {
    private Drawable backgroundView;
    private GradientColor gradientColor;
    private String textMessage;
    private String buttonMessage;
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
