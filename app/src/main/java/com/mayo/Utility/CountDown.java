package com.mayo.Utility;

import android.content.Context;
import android.os.CountDownTimer;
import com.mayo.classes.CustomViewPager;
import com.mayo.models.MapDataModel;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 02/02/18.
 */

public class CountDown {
    private Context mContext;
    private CustomViewPager mCustomViewPager;
    private ArrayList<MapDataModel> mMapDataModels;

    public CountDown(Context pContext, long pMilliseconds, long pCountDownInterval, CustomViewPager pCustomViewPager,
                     ArrayList<MapDataModel> pMapDataModels) {
        mContext = pContext;
        mCustomViewPager = pCustomViewPager;
        mMapDataModels = pMapDataModels;
        new CountDownTimerClass(pMilliseconds,pCountDownInterval);
    }

    public class CountDownTimerClass extends CountDownTimer {
        CountDownTimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (mCustomViewPager != null && mMapDataModels.size() > 1) {
                mCustomViewPager.setCurrentItem(1);
            }
        }
    }
}
