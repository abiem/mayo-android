package com.mayo.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

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
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.application.MayoApplication;
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.MapDataModel;
import com.mayo.viewclasses.CustomViewPager;

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
    private TextView mImageTextView, mTextView;
    private LinearLayout mChatPopupLayout;
    private ImageButton mImageButton;
    private EditText mEditText, mEdiTextNew;
    private Button mTextButton;
    private CardView mCardView;
    private MayoApplication mMayoApplication;

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
        Constants.CardType cardType = Constants.CardType.values()[position];
        switch (cardType) {
            case POST:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_one, collection, false);
                mTextButton = (Button) layout.findViewById(R.id.textbutton);
                mEditText = (EditText) layout.findViewById(R.id.postEditText);
                mEdiTextNew = (EditText) layout.findViewById(R.id.editText);
                mImageButton = (ImageButton) layout.findViewById(R.id.imagebutton);
                mCardView = (CardView) layout.findViewById(R.id.mapcardView);
                mEditText.setCursorVisible(false);
                break;
            case FAKECARDONE:
            case FAKECARDTWO:
            case FAKECARDTHREE:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_two, collection, false);
                mImageTextView = (TextView) layout.findViewById(R.id.imageTextView);
                mChatPopupLayout = (LinearLayout) layout.findViewById(R.id.chatPopupLayout);
                mTextView = (TextView) layout.findViewById(R.id.viewText);
                break;
            case DEFAULT:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_three, collection, false);
                break;
        }
        LinearLayout backgroundView = (LinearLayout) layout.findViewById(R.id.backgroundmapviewpager);
        int Type = mMapDataModelArrayList.get(position).getFakeCardPosition();
        Constants.CardType cardTypeCheck = Constants.CardType.values()[Type];
        switch (cardTypeCheck) {
            case POST: //first card
                mCardView.setVisibility(View.INVISIBLE);
                mImageTextView.setVisibility(View.GONE);
                mEditText.setCursorVisible(false);
                mTextButton.setOnClickListener(this);
                mImageButton.setOnClickListener(this);
                mEditText.setOnFocusChangeListener(this);
                mEdiTextNew.setOnFocusChangeListener(this);
                mEditText.addTextChangedListener(this);
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
            default:
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
                    mEditText.setText(Constants.sConstantString);
                    mEditText.setHint(mContext.getResources().getString(R.string.help_message));
                    mEditText.setCursorVisible(false);
                    mEdiTextNew.requestFocus();
                    mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                }
                break;
        }

        mClickListener.onClick(v, position);
    }

    public void setCardViewVisible() {
        if (mCardView != null) {
            mCardView.setVisibility(View.VISIBLE);
            mEdiTextNew.requestFocus();
        }
    }

    public void deleteItemFromArrayList(int pIndex) {
        if (pIndex >= 0 && pIndex < mMapDataModelArrayList.size()) {
            mMapDataModelArrayList.remove(pIndex);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.postEditText:
                if (hasFocus) {
                    mEditText.setHint("");
                    mEditText.setCursorVisible(true);
                } else {
                    mEditText.setHint(mContext.getResources().getString(R.string.help_message));
                    mEditText.setCursorVisible(false);
                }
                break;

            case R.id.editText:
                mMayoApplication.hideKeyboard(mActivity.getCurrentFocus());
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditText.setCursorVisible(false);
        mTextButton.setSelected(false);
        mTextButton.setAlpha(Constants.sTransparencyLevelFade);
        mImageButton.setAlpha(Constants.sTransparencyLevelFade);
        if (!mEditText.getText().toString().isEmpty()) {
            mTextButton.setEnabled(true);
            mTextButton.setSelected(true);
            mEditText.setCursorVisible(true);
            mTextButton.setAlpha(Constants.sNonTransparencyLevel);
            mImageButton.setAlpha(Constants.sNonTransparencyLevel);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
