package com.mayo.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.sahasbhop.apngview.ApngImageLoader;

import org.androidannotations.annotations.EApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Lakshmikodali on 02/01/18.
 */
@SuppressLint("Registered")
@EApplication
public class MayoApplication extends MultiDexApplication {
    AppCompatActivity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        configApng();
    }

    private void configApng() {
        ApngImageLoader apngImageLoader = ApngImageLoader.getInstance();
        apngImageLoader.setEnableDebugLog(false);
        apngImageLoader.setEnableVerboseLog(false);
        apngImageLoader.init(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppCompatActivity getActivity() {
        return this.mActivity;
    }

    public void setActivity(AppCompatActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void showToast(Context mContext, String mMessage) {
        Toast.makeText(mContext, "" + mMessage, Toast.LENGTH_SHORT).show();
    }

    public void showSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
