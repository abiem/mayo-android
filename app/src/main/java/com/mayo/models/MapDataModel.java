package com.mayo.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MapDataModel {
    private Drawable backgroundView;
    private GradientColor gradientColor;
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
}
