package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.viewclasses.CustomViewPager;
import com.mayo.adapters.ViewPagerAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.interfaces.ButtonClickListener;
import com.mayo.models.TutorialModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import java.util.ArrayList;

import static com.mayo.Utility.CommonUtility.isLocationEnabled;

@SuppressLint("Registered")
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements ButtonClickListener {
    @App
    MayoApplication mMayoApplication;

    @ViewById(R.id.viewPagerTutorial)
    CustomViewPager mCustomViewPager;

    ArrayList<TutorialModel> tutorialModels;

    @AfterViews
    protected void init() {
        mMayoApplication.setActivity(this);
        setTutorialArray();
        setViewPager();
    }

    private void setTutorialArray() {
        tutorialModels = new ArrayList<>();
        TutorialModel tutorialModelOne = new TutorialModel();
        tutorialModelOne.setTextMessage(getResources().getString(R.string.tutorial1));
        tutorialModelOne.setButtonMessage(getResources().getString(R.string.button_text_tutorial_one));
        tutorialModelOne.setBackgroundView(ContextCompat.getDrawable(this, R.drawable.background_tutorial_one));
        tutorialModels.add(tutorialModelOne);
        TutorialModel tutorialModelTwo = new TutorialModel();
        tutorialModelTwo.setTextMessage(getResources().getString(R.string.tutorial2));
        tutorialModelTwo.setButtonMessage(getResources().getString(R.string.button_text_tutorial_second));
        tutorialModelTwo.setBackgroundView(ContextCompat.getDrawable(this, R.drawable.background_tutorial_second));
        tutorialModels.add(tutorialModelTwo);
        TutorialModel tutorialModelThree = new TutorialModel();
        tutorialModelThree.setTextMessage(getResources().getString(R.string.tutorial3));
        tutorialModelThree.setButtonMessage(getResources().getString(R.string.button_text_tutorial_third));
        tutorialModelThree.setBackgroundView(ContextCompat.getDrawable(this, R.drawable.background_tutorial_third));
        tutorialModels.add(tutorialModelThree);
        TutorialModel tutorialModelFour = new TutorialModel();
        tutorialModelFour.setTextMessage(getResources().getString(R.string.tutorial4));
        tutorialModelFour.setButtonMessage(getResources().getString(R.string.button_text_tutorial_fourth));
        tutorialModelFour.setBackgroundView(ContextCompat.getDrawable(this, R.drawable.background_tutorial_fourth));
        tutorialModels.add(tutorialModelFour);

    }

    private void setViewPager() {
        mCustomViewPager.setPagingEnabled(false);
        mCustomViewPager.setClipToPadding(false);
        mCustomViewPager.setPadding(48, 0, 48, 0);
        mCustomViewPager.setPageMargin(24);
        mCustomViewPager.setAdapter(new ViewPagerAdapter(this, this, tutorialModels));
        mCustomViewPager.setCurrentItem(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.sKeyPermissionCodeForLocation) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showMapActivity();
            } else {
                disableLocationDialogView();
            }
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(com.mayo.Utility.Constants.sKeyForPackage + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
    }

    private void showMapActivity() {
        if (isLocationEnabled(this)) {
            MapActivity_.intent(MainActivity.this).start();
            finish();
        } else {
            CommonUtility.showDialog(this, getResources().getString(R.string.open_location_settings));
        }
    }

    @Override
    public void onClick(View v) {
        switch (String.valueOf(v.getTag())) {
            case "0":
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                break;
            case "1":
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                break;
            case "2":
                locationDialogView();
                break;
            case "3":
                //below API level 21
                if (CommonUtility.askForPermissionLocation(this)) {
                    showMapActivity();
                }
                break;

        }
    }

    private void locationDialogView() {
        final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.location_dialog_view);
        if (dialog != null) {
            Button gotItButton = (Button) dialog.findViewById(R.id.gotitbutton);
            gotItButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                }
            });
        }
    }

    private void disableLocationDialogView() {
        final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.location_disable_dialog_view);
        if (dialog != null) {
            Button NotNowButton = (Button) dialog.findViewById(R.id.notnowbutton);
            Button SettingButton = (Button) dialog.findViewById(R.id.settingsbutton);
            NotNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            SettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSettings();
                }
            });
        }
    }
}


