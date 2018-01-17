package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cunoraz.gifview.library.GifView;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.firebase.auth.AnonymousAuth;
import com.mayo.viewclasses.CustomViewPager;
import com.mayo.adapters.IntroViewPagerAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.interfaces.ClickListener;
import com.mayo.models.TutorialModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import java.util.ArrayList;

@SuppressLint("Registered")
@EActivity(R.layout.activity_intro)
public class IntroActivity extends AppCompatActivity implements ClickListener {
    @App
    MayoApplication mMayoApplication;

    @ViewById(R.id.viewPagerTutorial)
    CustomViewPager mCustomViewPager;

    @ViewById(R.id.imageintroview)
    GifView imageIntroView;

    ArrayList<TutorialModel> tutorialModels;

    @AfterViews
    protected void init() {
        imageIntroView.setVisibility(View.GONE);
        if (!CommonUtility.getTutorialDone(this)) {
            mMayoApplication.setActivity(this);
            setTutorialArray();
            setViewPager();
            AnonymousAuth.signInAnonymously(this);
        } else {
            showMapActivity();
        }
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
        mCustomViewPager.setPadding(64, 80, 64, 80);
        mCustomViewPager.setPageMargin(24);
        mCustomViewPager.setAdapter(new IntroViewPagerAdapter(this, this, tutorialModels));
        mCustomViewPager.setCurrentItem(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.sKeyPermissionCodeForLocation) {
            CommonUtility.setTutorialDone(true, this);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showMapActivity();
            } else {
                showMapActivity();
            }
        }
    }


    private void showMapActivity() {
        openMapActivity();
        finish();
    }

    @Override
    public void onClick(View v) {
        Constants.TutorialViewScreen whichView = Constants.TutorialViewScreen.values()[Integer.parseInt(v.getTag().toString())];
        switch (whichView) {
            case FIRST:
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                break;
            case SECOND:
                imageIntroView.setVisibility(View.VISIBLE);
                //Glide.with(this).load(R.drawable.second_tutorial_pin).asGif().into(imageIntroView);
                imageIntroView.setGifResource(R.drawable.second_tutorial_pin);
                imageIntroView.play();
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                break;
            case THIRD:
                locationDialogView();
                break;
            case FOURTH:
                //below API level 21
                if (CommonUtility.askForPermissionLocation(this)) {
                    CommonUtility.setTutorialDone(true, this);
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
                    //Glide.with(IntroActivity.this).load(R.drawable.third_tutorial_pin).asGif().into(imageIntroView);
                    imageIntroView.setGifResource(R.drawable.third_tutorial_pin);
                    imageIntroView.play();
                    mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                }
            });
        }
    }


    private void openMapActivity() {
        imageIntroView.pause();
        MapActivity_.intent(IntroActivity.this).start();
    }
}


