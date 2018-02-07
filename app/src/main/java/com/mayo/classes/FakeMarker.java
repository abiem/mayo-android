package com.mayo.classes;

import android.content.Context;
import android.os.Handler;

import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
import com.mayo.models.FakeUsersShown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lakshmi on 07/02/18.
 */

public class FakeMarker {

    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();
    Context mContext;
    ArrayList<FakeUsersShown> mFakeUserShown;

    public FakeMarker(Context pContext, ArrayList<FakeUsersShown> pFakeUserShown) {
        mContext = pContext;
        mFakeUserShown = pFakeUserShown;
    }

    public static int generateRandomMarkerOfFakeUsers() {
        // generate random number between 2 to 4
        Random r = new Random();
        return r.nextInt(Constants.FakeUsersValues.sMaximumFakeusers - Constants.FakeUsersValues.sMinimumFakeusers)
                + Constants.FakeUsersValues.sMinimumFakeusers;
    }

    public static int generateEndTimeOfMarkerShown() {
        // generate random number between 1 to 6
        Random r = new Random();
        return r.nextInt(Constants.FakeUsersValues.sMaximumFakeUserTimeShown - Constants.FakeUsersValues.sMinimumFakeUserTimeShown)
                + Constants.FakeUsersValues.sMinimumFakeUserTimeShown;
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 1000); //

    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        for (int i = 0; i < mFakeUserShown.size(); i++) {
                            if (mFakeUserShown.get(i).getEndTime().before(CommonUtility.getCurrentTime())) {
                                ((MapActivity) mContext).removeFakeMarkerAccodingToTime(i);
                            }
                        }
                    }
                });
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

}
