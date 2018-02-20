package com.mayo.classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.view.View;

import com.mayo.Utility.CommonUtility;
import com.mayo.activities.MapActivity;

/**
 * Created by Lakshmi on 19/02/18.
 */

public class AnimateCard {
    private Context mContext;
    private CardView mCardView;
    private AnimatorSet mAnimatorSet;
    private CustomViewPager mCustomViewPager;

    public AnimateCard(Context pContext, CardView pCardView,CustomViewPager pCustomViewPagerMap) {
        mContext = pContext;
        mCardView = pCardView;
        mCustomViewPager=pCustomViewPagerMap;
    }

    public void playFadeInOutAnimation() {
        new CountDown(6000, 1000);
        mAnimatorSet = CommonUtility.fadeInOutAnimation(mCardView);
        mCardView.setVisibility(View.VISIBLE);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorSet.start();
            }
        });
        mAnimatorSet.start();
    }

    private class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onFinish() {
            if (mAnimatorSet != null) {
                mAnimatorSet.cancel();
                mAnimatorSet = null;
            }
            mCardView.setVisibility(View.GONE);
            ((MapActivity) mContext).setViewPagerData();
        }

        @Override
        public void onTick(long duration) {
        }
    }
}
