package com.mayo.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

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
        Fabric.with(this, new Crashlytics());
        super.onCreate();
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
}
