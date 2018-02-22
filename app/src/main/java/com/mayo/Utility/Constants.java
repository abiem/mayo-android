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
    public static final int sTaskExpiryTime = 1000 * 60 * 60; //3600seconds=1hour

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
