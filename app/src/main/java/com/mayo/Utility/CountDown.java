package com.mayo.Utility;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lakshmi on 02/02/18.
 */

public class CountDown {
    private Context mContext;

    CountDown(Context pContext) {
        mContext = pContext;
    }

    public class CountDownTimer extends android.os.CountDownTimer {
        CountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    }

    public void onFinishCountDown() {

    }
}
