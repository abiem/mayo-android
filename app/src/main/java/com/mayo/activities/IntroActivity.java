package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

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

    @ViewById(R.id.rotateimage)
    ImageView rotateImage;

    @ViewById(R.id.imageHandsView)
    GifView imageHandView;

    @ViewById(R.id.imagePins)
    GifView imagePins;


    ArrayList<TutorialModel> tutorialModels;
    CountDown mCountDown;
    boolean isHandViewShown;

    @AfterViews
    protected void init() {
        imageHandView.setVisibility(View.GONE);
        imageHandView.setGifResource(R.drawable.thanks);
        imagePins.setGifResource(R.drawable.newpin);
        if (!CommonUtility.getTutorialDone(this)) {
            mMayoApplication.setActivity(this);
            setTutorialArray();
            setViewPager();
            //AnonymousAuth.signInAnonymously(this);
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
                playGifImage();
                mCountDown = new CountDown(3500, 1000);
                break;
            case SECOND:
                rotateImage.clearAnimation();
                rotateImage.setVisibility(View.GONE);
                pauseGifImage();
                playGifImageSecond();
                mCountDown.cancel();
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                break;
            case THIRD:
                locationDialogView();
                mCountDown.cancel();
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
                    pauseGifImageSecond();
                    imageHandView.setGifResource(R.drawable.thirdpin);
                    playGifImage();
                    mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                }
            });
        }
    }


    private void openMapActivity() {
        imageHandView.pause();
        MapActivity_.intent(IntroActivity.this).start();
    }

    private class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onFinish() {
            if (isHandViewShown) {
                imageHandView.setVisibility(View.GONE);
            }
            mCountDown.cancel();
            if (!isHandViewShown) {
                rotateImage.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.rotate);
                rotateImage.startAnimation(animation);
                mCountDown = new CountDown(2500, 1000);
                isHandViewShown = true;
            }
        }

        @Override
        public void onTick(long duration) {
        }
    }

    private void playGifImage() {
        imageHandView.setVisibility(View.VISIBLE);
        imageHandView.play();
    }

    private void pauseGifImage() {
        imageHandView.pause();
        imageHandView.setVisibility(View.GONE);
    }

    private void playGifImageSecond() {
        imagePins.setVisibility(View.VISIBLE);
        imagePins.play();
    }

    private void pauseGifImageSecond() {
        imagePins.pause();
        imagePins.setVisibility(View.GONE);
    }
}

