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
    public static final int sKeyCameraZoom = 16;

    //setting package
    static final String sKeyForPackage = "package:";

    public static final int sKeyForMapRadius = 200;

    public static final float sTransparencyLevel = 0.5f;
    public static final float sTransparencyLevelBackground = 0.9f;

    class sharedPreferences {
        static final String sTutorialDone = "isTutorialDone";
        static final String sUserId = "firebaseUserId";
        static final String sDeviceToken = "deviceToken";
        static final String sSoftKeyBoard= "softKeyBoardShown";
    }

    public enum TutorialViewScreen {
        FIRST, SECOND, THIRD, FOURTH
    }


}
