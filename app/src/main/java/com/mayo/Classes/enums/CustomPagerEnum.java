package com.mayo.Classes.enums;

import com.mayo.R;

/**
 * Created by Lakshmikodali on 08/01/18.
 */

public enum CustomPagerEnum {

    FIRST(R.string.first_tutorial_screen, R.layout.first_tutorial_screen),
    SECOND(R.string.second_tutorial_screen, R.layout.second_tutorial_screen),
    THIRD(R.string.third_tutorial_screen, R.layout.third_tutorial_screen),
    FOURTH(R.string.fourth_tutorial_screen, R.layout.fourth_tutorial_screen);

    private int mTitleResId;
    private int mLayoutResId;

    CustomPagerEnum(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
