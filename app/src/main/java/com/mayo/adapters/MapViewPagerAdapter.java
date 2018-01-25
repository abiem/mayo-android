package com.mayo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.InputType;
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
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.MapDataModel;
import com.mayo.viewclasses.CustomViewPager;

import java.util.ArrayList;


/**
 * Created by Lakshmikodali on 14/01/18.
 */

public class MapViewPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<MapDataModel> mMapDataModelArrayList;
    private ViewClickListener mClickListener;
    private CustomViewPager mCustomViewPager;
    private TextView mViewText, mImageTextView;
    private LinearLayout mParentImageButton, mChatPopupLayout;
    private ImageButton mImageButton;
    private EditText mEditText;
    private Button mTextButton;
    private CardView mCardView;

    public MapViewPagerAdapter(Context pContext, ArrayList<MapDataModel> pMapDataModel, ViewClickListener pClickListener, CustomViewPager pCustomViewPager) {
        mContext = pContext;
        mMapDataModelArrayList = pMapDataModel;
        mClickListener = pClickListener;
        mCustomViewPager = pCustomViewPager;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = null;
        switch (position) {
            case 0:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_one, collection, false);
                mParentImageButton = (LinearLayout) layout.findViewById(R.id.parentimagebutton);
                mTextButton = (Button) layout.findViewById(R.id.textbutton);
                mEditText = (EditText) layout.findViewById(R.id.postEditText);
                mImageButton = (ImageButton) layout.findViewById(R.id.imagebutton);
                mCardView = (CardView) layout.findViewById(R.id.mapcardView);
                break;
            default:
                layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_two, collection, false);
                mImageTextView = (TextView) layout.findViewById(R.id.imageTextView);
                mImageButton = (ImageButton) layout.findViewById(R.id.imageButtonHelp);
                mChatPopupLayout = (LinearLayout) layout.findViewById(R.id.chatPopupLayout);
                break;
        }
        LinearLayout backgroundView = (LinearLayout) layout.findViewById(R.id.backgroundmapviewpager);
        mViewText = (TextView) layout.findViewById(R.id.viewText);
        mViewText.setText(mMapDataModelArrayList.get(position).getTextMessage());
        switch (position) {
            case 0: //first card
                mCardView.setVisibility(View.INVISIBLE);
                mImageTextView.setVisibility(View.GONE);
                mImageButton.setAlpha(Constants.sTransparencyLevelFade);
                mViewText.setAlpha(Constants.sTransparencyLevelFade);
                mTextButton.setAlpha(Constants.sTransparencyLevelFade);
                mViewText.setOnClickListener(this);
                mTextButton.setOnClickListener(this);
                mImageButton.setOnClickListener(this);
                break;
            case 1: //first fake card
                mChatPopupLayout.setVisibility(View.GONE);
                break;
            case 2: //second fake card
                mChatPopupLayout.setVisibility(View.VISIBLE);
                mChatPopupLayout.setOnClickListener(this);
                break;
            case 3: //third fake card
                mChatPopupLayout.setVisibility(View.GONE);
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
            case R.id.viewText:
                mEditText.setVisibility(View.VISIBLE);
                mViewText.setVisibility(View.GONE);
                mEditText.setCursorVisible(true);
                break;
            case R.id.imagebutton:
                mEditText.setVisibility(View.GONE);
                mViewText.setVisibility(View.VISIBLE);
                break;
        }

        mClickListener.onClick(v, position);
    }

    public void setCardViewVisible() {
        if (mCardView != null)
            mCardView.setVisibility(View.VISIBLE);
    }

    public void deleteItemFromArrayList(int pIndex) {
        mMapDataModelArrayList.remove(pIndex);
        notifyDataSetChanged();
    }
}
