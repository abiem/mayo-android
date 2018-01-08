package com.mayo.Classes;

import com.mayo.R;

import java.util.Random;

/**
 * Created by Lakshmikodali on 07/01/18.
 */
class CardColor {

    // array of arrays of color choices
    // first is start color
    // second is end color
    int[][] choices = {
            {R.color.colorDarkLightGreen, R.color.colorGreen}, // dark green 1 to light green 1
            {R.color.colorLightBlue, R.color.colorPink}, // light blue 1 to pink 1
            {R.color.colorRed, R.color.colorOrange}, // red 1 to orange 1
            {R.color.colorDarkGreen, R.color.colorLightGreen},// dark green 2 to light green 2
            {R.color.colorPurple, R.color.colorNewPink}, // purple to pink
            {R.color.colorDarkPink, R.color.colorGrey}, // pink to grey
            {R.color.colorOffYellow, R.color.colorYellow}, // off yellow to yellow
            {R.color.colorDarkGreenNew, R.color.colorLightGreenNew}, // dark green 3 to light green 3
            {R.color.colorDarkBrown, R.color.colorLightBrown}, // dark brown to light brown
            {R.color.colorDarkBlue, R.color.colorLightBlueNew}, // dark blue to light blue
            {R.color.colorRed, R.color.colorOrange} // dark red to light orange
    };

    int[] expireCard = {R.color.colorLightGrey, R.color.colorLightWhite};

    private int generateRandomNumber() {
        // generate random number between 0 and length of color choices
        Random random = new Random();
        int numChoose = random.nextInt(choices.length);
        return choices[numChoose][0];
    }

    int generateRandomColor() {
        // generate random number
        int randomNumber = generateRandomNumber();

        // use the random number as index for the colors array

        // return generated color array
        return choices[randomNumber][0];
    }
}