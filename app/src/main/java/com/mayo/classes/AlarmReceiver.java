package com.mayo.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;

/**
 * Created by Lakshmi on 19/03/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("android.intent.action.MAIN");
        i.putExtra(Constants.Notifications.sAlarmManagerNotification, Constants.Notifications.sAlarmManager_Cmg);
        if (context != null) {
            context.sendBroadcast(i);
        }
    }
}
