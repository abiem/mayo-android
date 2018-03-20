package com.mayo.firebase.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity_;
import com.mayo.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = null, body = null, click_action = null;
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            click_action = remoteMessage.getNotification().getClickAction();
        }
        Map<String, String> data = remoteMessage.getData();
        JSONObject jsonObject = new JSONObject(data);
        try {
            int notificationType = jsonObject.getInt(Constants.Notifications.sNotification_Type);
            switch (notificationType) {
                case Constants.Notifications.sNOTIFICATION_MESSAGE:
                    String senderId = jsonObject.getString(Constants.Notifications.sSenderId);
                    if (senderId != null) {
                        String currentUserId = CommonUtility.getUserId(this);
                        if (currentUserId != null && senderId.equals(currentUserId)) {
                            return;
                        } else {
                            String channelId = jsonObject.getString(Constants.Notifications.sChannelId);
                            if (title != null && body != null && click_action != null) {
                                setNotification(title, body, click_action, channelId);
                            }
                        }
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_WERE_THANKS:
                    if (title != null && body != null) {
                        setNotificationForNearByTask(title, body);
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED:
                    break;
                case Constants.Notifications.sNOTIFICATION_NEARBY_TASK:
                    if (title != null && body != null) {
                        setNotificationForNearByTask(title, body);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotification(String pMessageBody, String pSubTitle, String pClickAction, String pChannelId) {
        NotificationCompat.Builder builder = CommonUtility.notificationBuilder(this, pMessageBody, pSubTitle);
        Intent notificationIntent = new Intent(pClickAction);
        notificationIntent.putExtra(Constants.Notifications.sChannelId, pChannelId);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    private void setNotificationForNearByTask(String pMessageBody, String pSubTitle) {
        NotificationCompat.Builder builder = CommonUtility.notificationBuilder(this, pMessageBody, pSubTitle);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

}
