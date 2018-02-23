package com.mayo.firebase.services;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mayo.Utility.CommonUtility;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        JSONObject jsonObject = new JSONObject(data);
    }

    private void setNotification(String pMessageBody,String pSubTitle) {
        NotificationCompat.Builder builder = CommonUtility.notificationBuilder(this, pMessageBody,pSubTitle);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}
