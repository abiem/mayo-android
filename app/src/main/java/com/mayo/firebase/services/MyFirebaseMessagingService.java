package com.mayo.firebase.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.firebase.database.FirebaseDatabase;

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
                            if (channelId != null) {
                                processMessageNotification(channelId);
                            }
                        }
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_WERE_THANKS:
                    break;
                case Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED:
                    break;
                case Constants.Notifications.sNOTIFICATION_NEARBY_TASK:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotification(String pMessageBody, String pSubTitle) {
        NotificationCompat.Builder builder = CommonUtility.notificationBuilder(this, pMessageBody, pSubTitle);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    private void processMessageNotification(String pChannelId) {
        Intent i = new Intent("android.intent.action.MAIN");
        i.putExtra(Constants.Notifications.sChannelId, pChannelId);
        sendBroadcast(i);
    }
}
