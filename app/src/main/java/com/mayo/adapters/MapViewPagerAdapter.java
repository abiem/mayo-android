package com.mayo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.application.MayoApplication;
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.MapDataModel;
import com.mayo.classes.CustomViewPager;
import com.mayo.models.Task;

import java.util.ArrayList;


/**
 * Created by Lakshmikodali on 14/01/18.
 */

public class MapViewPagerAdapter extends PagerAdapter implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<MapDataModel> mMapDataModelArrayList;
    private ViewClickListener mClickListener;
    private CustomViewPager mCustomViewPager;
    private TextView mImageTextView, mTextView, mTextViewNew;
    private LinearLayout mChatPopupLayout, mDoneParentLayout, mPostParentLayout;
    private ImageButton mImageButton, mMessageButton, mDoneButton;
    private EditText mEditText, mEdiTextNew;
    private Button mTextButton;
    private CardView mCardView, mCardViewFakeCards, mCardViewNewCards;
    private MayoApplication mMayoApplication;
    private boolean isPostViewVisible = false;

    public MapViewPagerAdapter(Context pContext, ArrayList<MapDataModel> pMapDataModel, ViewClickListener pClickListener,
                               CustomViewPager pCustomViewPager, Activity pActivity, MayoApplication pMayoApplication) {
        mContext = pContext;
        mMapDataModelArrayList = pMapDataModel;
        mClickListener = pClickListener;
        mCustomViewPager = pCustomViewPager;
        mActivity = pActivity;
        mMayoApplication = pMayoApplication;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = null;
        int positionOfCardViewShown = position;
        if (mMapDataModelArrayList.get(position).getFakeCardPosition() == Constants.CardType.DEFAULT.getValue()) {
            positionOfCardViewShown = Constants.CardType.DEFAULT.getValue();
        }
        Constants.CardType cardType = Constants.CardType.values()[positionOfCardViewShown];
        switch (cardType) {
            case POST:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_one, collection, false);
                mTextButton = (Button) layout.findViewById(R.id.textbutton);
                mEditText = (EditText) layout.findViewById(R.id.postEditText);
                mEdiTextNew = (EditText) layout.findViewById(R.id.editText);
                mImageButton = (ImageButton) layout.findViewById(R.id.imagebutton);
                mCardView = (CardView) layout.findViewById(R.id.mapcardView);
                mPostParentLayout = (LinearLayout) layout.findViewById(R.id.parentButton);
                mDoneParentLayout = (LinearLayout) layout.findViewById(R.id.parentImagesButton);
                mMessageButton = (ImageButton) layout.findViewById(R.id.messageIcon);
                mDoneButton = (ImageButton) layout.findViewById(R.id.doneIcon);
                mMapDataModelArrayList.get(position).setCardView(mCardView);
                break;
            case FAKECARDONE:
            case FAKECARDTWO:
            case FAKECARDTHREE:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_two, collection, false);
                mCardViewFakeCards = (CardView) layout.findViewById(R.id.mapcardViewTwo);
                mImageTextView = (TextView) layout.findViewById(R.id.imageTextView);
                mChatPopupLayout = (LinearLayout) layout.findViewById(R.id.chatPopupLayout);
                mTextView = (TextView) layout.findViewById(R.id.viewText);
                mMapDataModelArrayList.get(position).setCardView(mCardViewFakeCards);
                break;
            case DEFAULT:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_three, collection, false);
                mTextViewNew = (TextView) layout.findViewById(R.id.viewTextOfThree);
                mCardViewNewCards = (CardView) layout.findViewById(R.id.mapcardViewThree);
                mMapDataModelArrayList.get(position).setCardView(mCardViewNewCards);
                break;
        }
        RelativeLayout backgroundView = (RelativeLayout) layout.findViewById(R.id.backgroundmapviewpager);
        int Type = mMapDataModelArrayList.get(position).getFakeCardPosition();
        Constants.CardType cardTypeCheck = Constants.CardType.values()[Type];
        switch (cardTypeCheck) {
            case POST: //first card
                mEditText.setCursorVisible(false);
                if (mMapDataModelArrayList.size() != 1) {
                    if (!isPostViewVisible && CommonUtility.getFakeCardShownOrNot(mContext)) {
                        mCardView.setVisibility(View.INVISIBLE);
                        isPostViewVisible = true;
                    }
                } else {
                    mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                    requestEditTextNew();
                }
                if (CommonUtility.getTaskApplied(mContext)) {
                    mCardView.setVisibility(View.VISIBLE);
                    mEditText.setEnabled(false);
                    mDoneParentLayout.setVisibility(View.VISIBLE);
                    mPostParentLayout.setVisibility(View.GONE);
                    Task task = CommonUtility.getTaskData(mContext);
                    Drawable drawable = CommonUtility.getGradientDrawable("#" + task.getStartColor(), "#" + task.getEndColor(), mContext);
                    mMapDataModelArrayList.get(position).setBackgroundView(drawable);
                    mEditText.setText(task.getTaskDescription());
                    requestEditTextNew();
                }
                mTextButton.setOnClickListener(this);
                mImageButton.setOnClickListener(this);
                mEditText.setOnFocusChangeListener(this);
                mEditText.addTextChangedListener(this);
                mMessageButton.setOnClickListener(this);
                mDoneButton.setOnClickListener(this);
                mTextButton.setAlpha(Constants.sTransparencyLevelFade);
                mImageButton.setAlpha(Constants.sTransparencyLevelFade);
                break;
            case FAKECARDONE: //first fake card
                mChatPopupLayout.setVisibility(View.GONE);
                mTextView.setText(mMapDataModelArrayList.get(position).getTextMessage());
                break;
            case FAKECARDTWO: //second fake card
                mChatPopupLayout.setVisibility(View.VISIBLE);
                mChatPopupLayout.setOnClickListener(this);
                mTextView.setText(mMapDataModelArrayList.get(position).getTextMessage());
                break;
            case FAKECARDTHREE: //third fake card
                mChatPopupLayout.setVisibility(View.GONE);
                mTextView.setText(mMapDataModelArrayList.get(position).getTextMessage());
                break;
            case DEFAULT: // all cards that is fetch from firebase
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mCardViewNewCards.setBackground(mMapDataModelArrayList.get(position).getBackgroundView());
                } else {
                    mCardViewNewCards.setBackgroundDrawable(mMapDataModelArrayList.get(position).getBackgroundView());
                }
                mTextViewNew.setText(mMapDataModelArrayList.get(position).getTextMessage());
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            backgroundView.setBackground(mMapDataModelArrayList.get(position).getBackgroundView());
        } else {
            backgroundView.setBackgroundDrawable(mMapDataModelArrayList.get(position).getBackgroundView());
        }
        collection.addView(layout);
        return layout;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public int getItemPosition(Object object) {
        int index = mMapDataModelArrayList.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }


    @Override
    public int getCount() {
        return mMapDataModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onClick(View v) {
        int position = mCustomViewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.imagebutton:
                if (!mEditText.getText().toString().isEmpty()) {
                    mEditText.setText(Constants.sConstantEmptyString);
                    mEditText.setHint(mContext.getResources().getString(R.string.help_message));
                    mEditText.setCursorVisible(false);
                    mEdiTextNew.requestFocus();
                    mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                }
                break;
            case R.id.textbutton:
                if (mTextButton.getAlpha() != Constants.sTransparencyLevelFade) {
                    mMayoApplication.hideKeyboard(mEditText);
                    CommonUtility.setSoftKeyBoardState(false, mContext);
                    mTextButton.setAlpha(Constants.sTransparencyLevelFade);
                    mImageButton.setAlpha(Constants.sTransparencyLevelFade);
                    mDoneParentLayout.setVisibility(View.VISIBLE);
                    mPostParentLayout.setVisibility(View.GONE);
                    mEditText.setCursorVisible(false);
                    mEditText.setEnabled(false);
                    mEditText.setText(mEditText.getText().toString());
                    mClickListener.onClick(v, position, mEditText.getText().toString());
                }
                break;
            case R.id.doneIcon:
                mClickListener.onClick(v, position, mEditText.getText().toString());
                break;
            case R.id.messageIcon:
                mClickListener.onClick(v, position, mEditText.getText().toString());
                break;
            case R.id.chatPopupLayout:
                mClickListener.onClick(v, position, Constants.sConstantEmptyString);
                break;
        }
    }

    private void requestEditTextNew() {
        mEdiTextNew.requestFocus();
        mEdiTextNew.setCursorVisible(false);
        CommonUtility.setSoftKeyBoardState(false, mContext);
        mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
    }

    public void setPostMessageView() {
        mEditText.setText(Constants.sConstantEmptyString);
        mEditText.setHint(mContext.getResources().getString(R.string.help_message));
        mEditText.setEnabled(true);
        mEdiTextNew.requestFocus();
        mPostParentLayout.setVisibility(View.VISIBLE);
        mDoneParentLayout.setVisibility(View.GONE);
    }

    public void setTaskCardViewVisible() {
        if (mCardView != null) {
            mCardView.setVisibility(View.VISIBLE);
            if (mEditText.getText().toString().isEmpty()) { //if no message is send to firebase
                mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                mEdiTextNew.requestFocus();
            } else {
                if ((!mEditText.getText().toString().isEmpty()) && CommonUtility.getTaskApplied(mContext)) {
                    mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                    CommonUtility.setSoftKeyBoardState(false, mContext);
                    return;
                }
                if ((!mEditText.getText().toString().isEmpty()) && mEditText.isEnabled()) {
                    mMayoApplication.showSoftKeyBoard();
                    CommonUtility.setSoftKeyBoardState(true, mContext);
                }
            }
        }
    }

    public void setPostCardText() {
        if (mEditText.getText().toString().isEmpty()) {
            mEditText.setText(Constants.sConstantEmptyString);
            mEditText.setHint(mContext.getResources().getString(R.string.help_message));
            mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
        }
        mEdiTextNew.requestFocus();
    }

    public void deleteItemFromArrayList(final int pIndex) {
        if (pIndex >= 1 && pIndex < mMapDataModelArrayList.size()) {
            if (mMapDataModelArrayList.get(pIndex).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                if (mCardView.getVisibility() != View.VISIBLE) {
                    isPostViewVisible = false;
                }
            }
            if (mMapDataModelArrayList.get(pIndex).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                if (mCardView.getVisibility() != View.VISIBLE) {
                    isPostViewVisible = false;
                }
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mMapDataModelArrayList.remove(pIndex);
                    notifyDataSetChanged();
                }
            }, 100);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.postEditText:
                if (hasFocus) {
                    mEditText.setHint("");
                    mEditText.setCursorVisible(true);
                    CommonUtility.setSoftKeyBoardState(true, mContext);
                } else {
                    mEditText.setHint(mContext.getResources().getString(R.string.help_message));
                    mEditText.setCursorVisible(false);
                    mEdiTextNew.requestFocus();
                    mMayoApplication.hideKeyboard(mEdiTextNew);
                    CommonUtility.setSoftKeyBoardState(false, mContext);
                }
                break;

            case R.id.editText:
                if (mEditText != null && mEditText.getText().toString().isEmpty()) {
                    mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditText.setCursorVisible(false);
        mTextButton.setAlpha(Constants.sTransparencyLevelFade);
        mImageButton.setAlpha(Constants.sTransparencyLevelFade);
        if (!mEditText.getText().toString().isEmpty()) {
            mEditText.setCursorVisible(true);
            mTextButton.setAlpha(Constants.sNonTransparencyLevel);
            mImageButton.setAlpha(Constants.sNonTransparencyLevel);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
