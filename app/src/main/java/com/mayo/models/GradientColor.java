package com.mayo.models;

import java.io.Serializable;

/**
 * Created by Lakshmikodali on 17/01/18.
 */

public class GradientColor implements Serializable {
    private String startColor;
    private String endColor;


    public String getStartColor() {
        return startColor;
    }

    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
    }
}