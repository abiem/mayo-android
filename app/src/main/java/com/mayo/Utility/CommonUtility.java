package com.mayo.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mayo.R;
import com.mayo.activities.MainActivity;

/**
 * Created by Lakshmikodali on 02/01/18.
 */

public class CommonUtility {
    private static AlertDialog mAlertDialog;
    private static Dialog mCustomDialog;

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
                                if (pContext instanceof MainActivity) {
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
            mCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(pActivity, R.color.colorDarkTransparent)));
            mCustomDialog.show();
            return mCustomDialog;
        }
        return null;
    }


}
