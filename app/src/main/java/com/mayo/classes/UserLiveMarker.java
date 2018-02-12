package com.mayo.classes;

import android.content.Context;
import android.os.Handler;

import com.mayo.Utility.CommonUtility;
import com.mayo.activities.MapActivity;
import com.mayo.models.UserMarker;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class UserLiveMarker {
    private Timer timer;
    private TimerTask timerTask;
    Handler handler = new Handler();
    Context mContext;
    HashSet<UserMarker> mUserLocationsMarker;

    public UserLiveMarker(Context pContext, HashSet<UserMarker> pUserLocations) {
        mContext = pContext;
        mUserLocationsMarker = pUserLocations;
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
                        for (UserMarker userMarker : mUserLocationsMarker) {
                            if (userMarker.getEndTime().before(CommonUtility.getCurrentTime())) {
                                ((MapActivity) mContext).removeUserMarkerAccordingToTime(userMarker);
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
