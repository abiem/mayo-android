package com.mayo.classes;

import android.content.Context;
import android.os.Handler;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.activities.MapActivity;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.models.UserMarker;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lakshmi on 22/02/18.
 */
/*
this is for uploading task from current user
 */
public class TaskTimer {
    Context mContext;
    private Timer mTimer;
    private TimerTask timerTask;
    private int mScheduleTime;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private Handler mHandler = new Handler();

    public TaskTimer(Context pContext, int pScheduleTime, MapViewPagerAdapter pMapViewPagerAdapter) {
        mContext = pContext;
        mScheduleTime = pScheduleTime;
        mMapViewPagerAdapter = pMapViewPagerAdapter;
    }

    public void startTimer() {
        //set a new Timer
        mTimer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        mTimer.schedule(timerTask, mScheduleTime, 10000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                mHandler.post(new Runnable() {
                    public void run() {
                        ((MapActivity) mContext).updateTaskData(mContext.getResources().getString(R.string.STATUS_FOR_TIME_EXPIRED)
                                , CommonUtility.getTaskData(mContext));
                        mMapViewPagerAdapter.setPostMessageView();
                        stoptimertask();
                    }
                });
            }
        };
    }

    public void stoptimertask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
    }
}
