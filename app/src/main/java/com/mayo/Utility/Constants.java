package com.mayo.Utility;

/**
 * Created by Lakshmikodali on 02/01/18.
 */

public class Constants {
    //messages
    static final String sOk = "Ok";

    //permission code
    public static final int sKeyPermissionCodeForLocation = 10001;

    //camera zoom
    public static final int sKeyCameraZoom = 19;

    //setting package
    static final String sKeyForPackage = "package:";

    //Radius in meters
    public static final int sKeyForMapRadius = 200;
    public static final double sKeyForMapRadiusInDouble = 0.2; //200 meters = 200/1000=0.2 meters
    //stroke width of circle
    public static final float sKeyForStrokeWidth = 50.0f;

    public static final float sTransparencyLevelFade = 0.5f;
    public static final float sNonTransparencyLevel = 1.0f;
    public static final String sConstantEmptyString = "";
    public static final String sConstantSpaceString = " ";
    public static final String sPostMessage = "postMessage";
    public static final String sQuestMessageShow = "isQuestMessageShow";
    public static final String sSmileCode = "\uD83D\uDE31";
    public static final long seconds = 6 * 60; //360 = 6 minutes
    public static final float sMarkerZIndexMaximum = 1.0f;
    public static final float sMarkerZIndexMinimum = 0.1f;
    public static final String sCardsData = "cardsData";
    public static final int sTaskExpiryTime = 1000 * 60 * 60; //3600seconds=1hour
    public static final int sLocationUpdateTime = 1000 * 60 * 10; //600seconds=10min
    public static final String sYes = "yes";

    public class Notifications {
        public static final int sNOTIFICATION_MESSAGE = 0;
        public static final int sNOTIFICATION_WERE_THANKS = 1;
        public static final int sNOTIFICATION_TOPIC_COMPLETED = 2;
        public static final int sNOTIFICATION_NEARBY_TASK = 3;
        public static final String sFCM_URL = "https://fcm.googleapis.com/fcm/send";
        public static final String sSERVER_KEY = "AAAAYgVZ9lU:APA91bExQ_X8TFudFkv5_5VJ9E70YQ6uB6hlZgkQENBNCOOZl8e_EHsD-WUkGQ2pFz78qZwLtwPvA_kJRNJfYK6r_tpANKwrOn7ZJeeVmCoJBLyO-aqOPQYEncPD05-UleyFfkiVYsPh";
        public static final String sTo = "to";
        public static final String sPriority = "priority";
        public static final String sBody = "body";
        public static final String sTitle = "title";
        public static final String sSound = "sound";
        public static final String sClickAction = "click_action";
        public static final String sNotification = "notification";
        public static final String sNotification_Type = "notification_type";
        public static final String sSenderId = "sender_id";
        public static final String sChannelId = "channelId";
        public static final String sTaskDescription = "task_description";
        public static final String sData = "data";
        public static final String sTaskId = "taskID";
        public static final String sAlarmManagerNotification = "alarmManagerNotification";
        public static final String sAlarmManager_Cmg = "alarmManagerNotificationComing";

        public static final String sNOTIFICATION_MESSAGE_App = "notification_message";
        public static final String sNOTIFICATION_WERE_THANKS_App = "notification_were_thanks";
        public static final String sNOTIFICATION_TOPIC_COMPLETED_App = "notification_topic_completed";
        public static final String sNOTIFICATION_NEARBY_TASK_App = "notification_nearby_task";
        public static final String sNOTIFICATION_TO_DEVICE = "notification_to_device";
        public static final String sNOTIFICATION_TO_TOPIC = "notification_to_topic";
    }

    public class GeoFencing {
        public static final String GEOFENCE_REQ_ID = "Geofence";
        public static final int sGEOFENCE_REQ_CODE = 0;
        public static final float sGeoFenceRadius = 200.0f;
        public static final float sGeoFenceDitance = 0.1000f;
    }

    public class AlarmManager {
        public static final int sAlarmCode = 101;
    }

    class sharedPreferences {
        static final String sTutorialDone = "isTutorialDone";
        static final String sUserId = "firebaseUserId";
        static final String sDeviceToken = "deviceToken";
        static final String sSoftKeyBoard = "softKeyBoardShown";
        static final String sFakeCardsShown = "isFakeCardShown";
        static final String sFakeCardOne = "FakeCardOne";
        static final String sFakeCardTwo = "FakeCardTwo";
        static final String sFakeCardThree = "FakeCardThree";
        static final String sCardPoints = "CardPoints";
        static final String sFakeMarkerShown = "isFakeMarkerShown";
        static final String sAnimationShownOnMap = "isAnimationShowingOnMap";
        static final String sTaskApplied = "isTaskApplied";
        static final String sTaskData = "taskData";
        static final String sTaskPosition_lat = "taskPosition_lat";
        static final String sTaskPosition_lng = "taskPosition_lng";
        static final String sUserLocation_lat = "UserLocation_lat";
        static final String sUserLocation_lng = "UserLocation_lng";
    }

    public class BroadCastReceiver {
        public static final String sBroadCastName = "googlegeofence";
    }

    public class FakeUsersValues {
        public static final int sMinimumFakeusers = 2;
        public static final int sMaximumFakeusers = 4;

        public static final int sMaximumFakeUserTimeShown = 6;
        public static final int sMinimumFakeUserTimeShown = 1;

        public static final double sFakeUserPositionMaximum = 0.00010;
        public static final double sFakeUserPositionMaximumNew = 0.00015;
        public static final double sFakeUserPositionMininum = -0.00010;
        public static final double sFakeUserPositionMininumNew = -0.00015;
        public static final double sFakeUserPositionConstant = 0.0;
        public static final double sFakeUserPositionConstantNew = 0.0005;
    }

    public class CardMarkerValues {
        public static final double sCardMarkerPositionMaximum = 0.00010;
        public static final double sCardMarkerPositionMaximumNew = 0.00015;
        public static final double sCardMarkerPositionMininum = -0.00010;
        public static final double sCardMarkerPositionMininumNew = -0.00015;
        public static final double sCardMarkerPositionConstant = 0.0;
        public static final double sCardMarkerPositionConstantNew = 0.0005;
    }

    public class CardPaddingValues {
        public static final int sTopBottomPadding = 80;
        public static final int sLeftRightPadding = 64;
    }

    public class CardMarginSetValues {
        public static final int sMarginValue = 24;
    }

    public enum TutorialViewScreen {
        FIRST, SECOND, THIRD, FOURTH
    }

    public enum PermissionDialog {
        AppPermissionDialog, LocationDialog
    }

    public enum FakeMarkerValues {
        FIRSTMARKER(1), SECONDMARKER(2), THIRDMARKER(3), FOURTHMARKER(4);
        int position;

        FakeMarkerValues(int position) {
            this.position = position;
        }

        public int getValue() {
            return position;
        }
    }

    public enum UserType {
        SELF, OTHER
    }

    public enum MessageFromLocalDevice {
        yes, no
    }

    public enum CardType {
        POST(0), FAKECARDONE(1), FAKECARDTWO(2), FAKECARDTHREE(3), DEFAULT(4);
        int position;

        CardType(int position) {
            this.position = position;
        }

        public int getValue() {
            return position;
        }
    }
}
