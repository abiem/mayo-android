package com.mayo.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mayo.R;
import com.mayo.activities.IntroActivity;
import com.mayo.viewclasses.CardColor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Lakshmikodali on 02/01/18.
 */

public class CommonUtility {
    private static AlertDialog mAlertDialog;
    private static Dialog mCustomDialog;
    private static SharedPreferences mSharedPreferences;
    private static SimpleDateFormat utcTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

    private static void initializeSharedPreference(Context context) {
        CommonUtility.mSharedPreferences = context.getSharedPreferences(null, Context.MODE_PRIVATE);
    }

    // Save if user tutorial is done
    public static void setTutorialDone(boolean pValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sTutorialDone, pValue);
        editor.apply();
    }

    public static boolean getTutorialDone(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sTutorialDone, false);
    }

    public static void setUserId(String pId, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.sharedPreferences.sUserId, pId);
        editor.apply();
    }

    public static String getUserId(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getString(Constants.sharedPreferences.sUserId, "");
    }

    public static void setDeviceToken(String pToken, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.sharedPreferences.sDeviceToken, pToken);
        editor.apply();
    }

    public static String getDeviceToken(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getString(Constants.sharedPreferences.sDeviceToken, "");
    }

    public static void setSoftKeyBoardState(boolean pcheckState, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sSoftKeyBoard, pcheckState);
        editor.apply();
    }

    public static boolean getSoftKeyBoardState(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sSoftKeyBoard, false);
    }

    public static void setFakeCardShownOrNot(boolean psetValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sFakeCardsShown, psetValue);
        editor.apply();
    }


    //be default value is true, because installing first time build we need to set fake cards
    public static boolean getFakeCardShownOrNot(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sFakeCardsShown, true);
    }

    public static void setFakeCardOne(boolean psetValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sFakeCardOne, psetValue);
        editor.apply();
    }

    public static boolean getFakeCardOne(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sFakeCardOne, false);
    }

    public static void setFakeCardTwo(boolean psetValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sFakeCardTwo, psetValue);
        editor.apply();
    }

    public static void setPoints(int pValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.sharedPreferences.sCardPoints, pValue);
        editor.apply();
    }

    public static int getPoints(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getInt(Constants.sharedPreferences.sCardPoints, 0);
    }

    public static boolean getFakeCardTwo(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sFakeCardTwo, false);
    }

    public static void setFakeCardThree(boolean psetValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sFakeCardThree, psetValue);
        editor.apply();
    }

    public static boolean getFakeCardThree(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sFakeCardThree, false);
    }


    public static void setHandsAnimationShownOnMap(boolean psetValue, Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.sharedPreferences.sAnimationShownOnMap, psetValue);
        editor.apply();
    }

    public static boolean getHandsAnimationShownOnMap(Context pContext) {
        if (mSharedPreferences == null) {
            initializeSharedPreference(pContext);
        }
        return mSharedPreferences.getBoolean(Constants.sharedPreferences.sAnimationShownOnMap, false);
    }


    public static boolean askForPermissionLocation(Activity activity) {
        int permissionCheck;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            permissionCheck = ContextCompat.checkSelfPermission(activity, "Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += ContextCompat.checkSelfPermission(activity, "Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.sKeyPermissionCodeForLocation);//Any number
            }
            return false;
        }
        return true;
    }

    public static String[] checkPermissions() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,};
    }

    public static boolean googleServicesAvailable(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int available = api.isGooglePlayServicesAvailable(activity);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(available)) {
            Dialog dialog = api.getErrorDialog(activity, available, 0);
            dialog.show();
        } else {
            return false;
        }
        return false;
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (lm != null) {
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return !(!gps_enabled && !network_enabled);
        }
        return false;
    }

    public static void showDialog(final Context pContext, String pMessage) {
        if (pContext != null) {
            if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                mAlertDialog = new AlertDialog.Builder(pContext).create();
                mAlertDialog.setTitle(pContext.getResources().getString(R.string.app_name));
                mAlertDialog.setMessage(pMessage);
                mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, Constants.sOk,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (pContext instanceof IntroActivity) {
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    pContext.startActivity(myIntent);
                                }
                            }
                        });
                mAlertDialog.show();
            }
        }
    }

    public static Dialog showCustomDialog(final Activity pActivity, int pLayout) {
        mCustomDialog = new Dialog(pActivity);
        mCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (mCustomDialog.getWindow() != null) {
            lp.copyFrom(mCustomDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            mCustomDialog.setContentView(pLayout);
            mCustomDialog.getWindow().setAttributes(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCustomDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            mCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(pActivity, R.color.colorDarkTransparent)));
            mCustomDialog.show();
            return mCustomDialog;
        }
        return null;
    }

    public static void goToSettings(Context pContext) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(com.mayo.Utility.Constants.sKeyForPackage + pContext.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(myAppSettings);
    }

    public static void goToSettingsForGpsLocation(Context pContext) {
        Intent myAppSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(myAppSettings);
    }

    public static Bitmap drawableToBitmap(Drawable pDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        pDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        pDrawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getGradientDrawable(String pBottomColor, String pTopColor, Context pContext) {
        int colors[] = {Color.parseColor(pBottomColor), Color.parseColor(pTopColor)};
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(pContext.getResources().getDimension(R.dimen.rd_4));
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradient.setGradientCenter(70.0f, 0.0f);
        return gradient;
    }

    public static String convertLocalTimeToUTC() {
        Date date = null;
        utcTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = utcTimeFormat.parse(utcTimeFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return String.valueOf(date.getTime());
        }
        return null;
    }

    public static String getLocalTime() {
        return utcTimeFormat.format(new Date());
    }

    public static String timeFormat(long pMessage) {
        SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);
        return SIMPLE_DATE_FORMAT.format(pMessage);
    }

    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;
    }

}
