package com.mayo.viewclasses;

import com.mayo.R;

import java.util.Random;

/**
 * Created by Lakshmikodali on 07/01/18.
 */
public class CardColor {

    // array of arrays of color choices
    // first is start color
    // second is end color
    public static String[][] choices = {
            {"#4fb5b2", "#bee7c7"}, // dark green 1 to light green 1
            {"#08bbdb", "#d5c2e6"}, // light blue 1 to pink 1
            {"#e86d5d", "#e8c378"}, // red 1 to orange 1
            {"#9cd72f", "#90e2ad"},// dark green 2 to light green 2
            {"#c96dd8", "#ff91a5"}, // purple to pink
            {"#e87185", "#eae6f2"}, // pink to grey
            {"#bccb4c", "#f3e24d"}, // off yellow to yellow
            {"#1dae73", "#c1d96e"}, // dark green 3 to light green 3
            {"#ac664c", "#cda83d"}, // dark brown to light brown
            {"#508fbc", "#5ac7cf"}, // dark blue to light blue
            {"#e86d5d", "#e8c378"} // dark red to light orange
    };

    public static String[] expireCard = {"#888888", "#F1F1F1"};

    public static int generateRandomColor() {
        // generate random number between 0 and length of color choices
        Random random = new Random();
        return random.nextInt(choices.length);
    }

}