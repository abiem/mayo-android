package com.mayo.Notifications;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.mayo.Utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lakshmi on 15/03/18.
 */

public class PushNotificationManager {
    private FcmClient mClient;

    public PushNotificationManager() {
        mClient = new FcmClient();
        mClient.setAPIKey(Constants.Notifications.sSERVER_KEY);
    }

    private JSONObject setNotificationToDeviceForMessage(String pDeviceToken, String pCurrentUserId, String pChannelId, String pTopic) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, pDeviceToken);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, "Someone posted in " + pTopic);
            notificationObject.put(Constants.Notifications.sTitle, " New Message Posted");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sSenderId, pCurrentUserId);
            dataObject.put(Constants.Notifications.sChannelId, pChannelId);
            dataObject.put(Constants.Notifications.sTaskDescription, pTopic);
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_MESSAGE);

            obj.put(Constants.Notifications.sData, dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    private JSONObject setYouWereThankedNotification(String pDeviceToken, String pTaskDescription) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, pDeviceToken);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, pTaskDescription + " " + "was completed. Thanks for helping! ");
            notificationObject.put(Constants.Notifications.sTitle, "You were thanked!");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_WERE_THANKS);

            obj.put(Constants.Notifications.sData, dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private JSONObject setNearbyTaskNotification(String pDeviceToken, String pTaskId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, pDeviceToken);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, "Someone has a new quest nearby");
            notificationObject.put(Constants.Notifications.sTitle, "New quest available");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_NEARBY_TASK);
            dataObject.put(Constants.Notifications.sTaskId, pTaskId);

            obj.put(Constants.Notifications.sData, dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private JSONObject setNotificationToTopicOnCompletion(String pDeviceToken, String pTaskMessage, String pChannelId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, pDeviceToken);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, pTaskMessage + " was completed");
            notificationObject.put(Constants.Notifications.sTitle, "Nearby quest Completed");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED);
            dataObject.put(Constants.Notifications.sChannelId, pChannelId);

            obj.put(Constants.Notifications.sData, dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private JSONObject setNotificationToDevice(String pDeviceToken, String pTaskMessage, String pChannelId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, pDeviceToken);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, pTaskMessage + " was completed");
            notificationObject.put(Constants.Notifications.sTitle, "Nearby quest Completed");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED);
            dataObject.put(Constants.Notifications.sChannelId, pChannelId);

            obj.put(Constants.Notifications.sData, dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private JSONObject setNotificationToTopic(String pChannelId, String pTopic, String pCurrentUserId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.Notifications.sTo, "/topics/" + pChannelId);
            obj.put(Constants.Notifications.sPriority, "high");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put(Constants.Notifications.sBody, "Someone posted in " + pTopic);
            notificationObject.put(Constants.Notifications.sTitle, "New Message Posted");
            notificationObject.put(Constants.Notifications.sSound, "default");

            obj.put(Constants.Notifications.sNotification, notificationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put(Constants.Notifications.sNotification_Type, Constants.Notifications.sNOTIFICATION_MESSAGE);
            dataObject.put(Constants.Notifications.sChannelId, pChannelId);
            dataObject.put(Constants.Notifications.sSenderId, pCurrentUserId);
            dataObject.put(Constants.Notifications.sTaskDescription, pTopic);

            obj.put(Constants.Notifications.sData, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void sendNotificationToDeviceForMessage(String pDeviceToken, String pCurrentUserId, String pChannelId, String pTopic) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_MESSAGE_App, pDeviceToken, pCurrentUserId, pChannelId, pTopic);
    }

    public void sendNotificationToTopicOnCompletion(String pDeviceToken, String pTaskMessage, String pChannelId) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED_App, pDeviceToken, pTaskMessage, pChannelId);
    }

    public void sendNearbyTaskNotification(String pDeviceToken, String pTaskId) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_NEARBY_TASK_App, pDeviceToken, pTaskId);
    }

    public void sendYouWereThankedNotification(String pDeviceToken, String pTaskDescription) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_WERE_THANKS_App, pDeviceToken, pTaskDescription);
    }

    public void sendNotificationToDevice(String pDeviceToken, String pTaskMessage, String pChannelId) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_TO_DEVICE, pDeviceToken, pTaskMessage, pChannelId);
    }

    public void sendNotificationToTopic(String pChannelId, String pTopic, String pCurrentUserId) {
        SendPushNotification sendPushNotification = new SendPushNotification();
        sendPushNotification.execute(Constants.Notifications.sNOTIFICATION_TO_TOPIC, pChannelId, pTopic, pCurrentUserId);
    }


    @SuppressLint("StaticFieldLeak")
    class SendPushNotification extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String checkType = params[0];
            String pDeviceToken;
            String pChannelId;
            switch (checkType) {
                case Constants.Notifications.sNOTIFICATION_MESSAGE_App:
                    try {
                        pDeviceToken = params[1];
                        String pCurrentUserId = params[2];
                        pChannelId = params[3];
                        String pTopic = params[4];
                        if (mClient != null) {
                            mClient.pushNotify(setNotificationToDeviceForMessage(pDeviceToken, pCurrentUserId, pChannelId, pTopic));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_TOPIC_COMPLETED_App:
                    try {
                        pDeviceToken = params[1];
                        String pTaskMessage = params[2];
                        pChannelId = params[3];
                        if (mClient != null) {
                            mClient.pushNotify(setNotificationToTopicOnCompletion(pDeviceToken, pTaskMessage, pChannelId));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_NEARBY_TASK_App:
                    try {
                        if (mClient != null) {
                            pDeviceToken = params[1];
                            String pTaskId = params[2];

                            mClient.pushNotify(setNearbyTaskNotification(pDeviceToken, pTaskId));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_WERE_THANKS_App:
                    try {
                        pDeviceToken = params[1];
                        String pTaskDescription = params[2];
                        if (mClient != null) {
                            mClient.pushNotify(setYouWereThankedNotification(pDeviceToken, pTaskDescription));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_TO_DEVICE:
                    try {
                        pDeviceToken = params[1];
                        String pTaskMessage = params[2];
                        pChannelId = params[3];
                        if (mClient != null) {
                            mClient.pushNotify(setNotificationToDevice(pDeviceToken, pTaskMessage, pChannelId));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.Notifications.sNOTIFICATION_TO_TOPIC:
                    try {
                        pChannelId = params[1];
                        String pTopic = params[2];
                        String pCurrentUserId = params[3];
                        if (mClient != null) {
                            mClient.pushNotify(setNotificationToTopic(pChannelId, pTopic, pCurrentUserId));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return null;
        }
    }
}
