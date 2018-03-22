package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.github.sahasbhop.apngview.ApngDrawable;
import com.github.sahasbhop.apngview.ApngImageLoader;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.firebase.auth.AnonymousAuth;
import com.mayo.classes.CustomViewPager;
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
    ImageView imageHands;

    @ViewById(R.id.imageRippleNew)
    ImageView imageRipple;

    @ViewById(R.id.imagePinView)
    ImageView imagePin;

    ArrayList<TutorialModel> tutorialModels;
    CountDown mCountDown;
    boolean isHandViewShown;
    ApngDrawable mApngDrawable;
    private ArrayList<String> mApngImages;

    @AfterViews
    protected void init() {
        mApngImages = new ArrayList<>();
        setApngImages();
        if (!CommonUtility.getTutorialDone(this)) {
            mMayoApplication.setActivity(this);
            setTutorialArray();
            setViewPager();
            AnonymousAuth.signInAnonymously(this);
        } else {
            showMapActivity();
        }
    }

    private void setApngImages() {
        mApngImages.add("assets://apng/fist_bump_720p.png");
        mApngImages.add("assets://apng/ripple_seq_480p.png");
        mApngImages.add("assets://apng/fatpin_seq01_720p.png");
        mApngImages.add("assets://apng/fatpin_seq02_720p.png");
        ApngImageLoader.getInstance().displayImage(mApngImages.get(0), imageHands);
        ApngImageLoader.getInstance().displayImage(mApngImages.get(1), imageRipple);
        ApngImageLoader.getInstance().displayImage(mApngImages.get(2), imagePin);

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
        mCustomViewPager.setPadding(Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding,
                Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding);
        mCustomViewPager.setPageMargin(Constants.CardMarginSetValues.sMarginValue);
        mCustomViewPager.setAdapter(new IntroViewPagerAdapter(this, this, tutorialModels));
        mCustomViewPager.setCurrentItem(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.sKeyPermissionCodeForLocation) {
            CommonUtility.setTutorialDone(true, this);
            //grant permission result will handle in map activity
            showMapActivity();
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
                playHandsImage();
                mCountDown = new CountDown(2300, 1000);
                break;
            case SECOND:
                rotateImage.clearAnimation();
                pauseHandsImage();
                playRippleImage();
                rotateImageClearAnimation();
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                ApngImageLoader.getInstance().displayImage(mApngImages.get(2), imageHands);
                break;
            case THIRD:
                locationDialogView();
                rotateImageClearAnimation();
                imageHands.setVisibility(View.GONE);
                break;
            case FOURTH:
                rotateImageClearAnimation();
                imageHands.setVisibility(View.GONE);
                //below API level 21
                if (CommonUtility.askForPermissionLocation(this)) {
                    CommonUtility.setTutorialDone(true, this);
                    showMapActivity();
                }
                break;

        }
    }

    private void rotateImageClearAnimation() {
        mCountDown.cancel();
        rotateImage.setVisibility(View.GONE);
        rotateImage.clearAnimation();
    }

    private void locationDialogView() {
        if (!isFinishing()) {
            final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.location_dialog_view);
            if (dialog != null) {
                Button gotItButton = (Button) dialog.findViewById(R.id.gotitbutton);
                gotItButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        pauseRippleImage();
                        playPinImageFirst();
                        mApngDrawable = ApngDrawable.getFromView(imageRipple);
                        mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
                    }
                });
            }
        }
    }


    private void openMapActivity() {
        pausePinImageSecond();
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
                imageHands.setVisibility(View.GONE);
            }
            mCountDown.cancel();
            if (!isHandViewShown) {
                rotateImage.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.rotate);
                rotateImage.startAnimation(animation);
                mCountDown = new CountDown(1000, 1000);
                isHandViewShown = true;
            }
        }

        @Override
        public void onTick(long duration) {
        }
    }

    private void playHandsImage() {
        imageHands.setVisibility(View.VISIBLE);
        mApngDrawable = ApngDrawable.getFromView(imageHands);
        if (mApngDrawable == null)
            return;
        mApngDrawable.start();
    }


    private void playRippleImage() {
        imageRipple.setVisibility(View.VISIBLE);
        mApngDrawable = ApngDrawable.getFromView(imageRipple);
        if (mApngDrawable == null)
            return;
        mApngDrawable.setNumPlays(0);
        mApngDrawable.start();
    }

    private void pauseHandsImage() {
        if (mApngDrawable == null)
            return;
        if (mApngDrawable.isRunning()) {
            mApngDrawable.stop(); // Stop animation
        }
        imageHands.setVisibility(View.GONE);
    }

    private void pauseRippleImage() {
        if (mApngDrawable == null)
            return;
        if (mApngDrawable.isRunning()) {
            mApngDrawable.stop(); // Stop animation
        }
        imageRipple.setVisibility(View.GONE);
    }

    private void pausePinImageFirst() {
        if (mApngDrawable == null)
            return;
        if (mApngDrawable.isRunning()) {
            mApngDrawable.stop(); // Stop animation
        }
        imagePin.setVisibility(View.GONE);
    }

    private void playPinImageFirst() {
        imagePin.setVisibility(View.VISIBLE);
        mApngDrawable = ApngDrawable.getFromView(imagePin);
        if (mApngDrawable == null)
            return;
        mApngDrawable.setNumPlays(1);
        mApngDrawable.start();
        ApngImageLoader.getInstance().displayImage(mApngImages.get(3), imageRipple);
        new CountDownNew(2300, 1000);
    }


    private void playPinImageSecond() {
        imagePin.setVisibility(View.GONE);
        imageRipple.setVisibility(View.VISIBLE);
        mApngDrawable = ApngDrawable.getFromView(imageRipple);
        if (mApngDrawable == null)
            return;
        mApngDrawable.setNumPlays(0);
        mApngDrawable.start();
    }

    private void pausePinImageSecond() {
        if (mApngDrawable == null)
            return;
        if (mApngDrawable.isRunning()) {
            mApngDrawable.stop(); // Stop animation
        }
    }

    private class CountDownNew extends CountDownTimer {
        CountDownNew(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onFinish() {
            pausePinImageFirst();
            playPinImageSecond();
        }

        @Override
        public void onTick(long duration) {
        }
    }
}

