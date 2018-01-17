package com.mayo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mayo.R;
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
    private TextView mViewText;
    private TextView mBelowViewPagerText;
    private LinearLayout mParentImageButton;
    private ImageButton mImageButton;
    private EditText mEditText;
    private Button mTextButton;

    public MapViewPagerAdapter(Context pContext, ArrayList<MapDataModel> pMapDataModel, ViewClickListener pClickListener, CustomViewPager pCustomViewPager) {
        mContext = pContext;
        mMapDataModelArrayList = pMapDataModel;
        mClickListener = pClickListener;
        mCustomViewPager = pCustomViewPager;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen_one, collection, false);
        LinearLayout backgroundView = (LinearLayout) layout.findViewById(R.id.backgroundmapviewpager);
        mViewText = (TextView) layout.findViewById(R.id.viewText);
        mBelowViewPagerText = (TextView) layout.findViewById(R.id.msg);
        mParentImageButton = (LinearLayout) layout.findViewById(R.id.parentimagebutton);
        mImageButton = (ImageButton) layout.findViewById(R.id.imagebutton);
        mTextButton = (Button) layout.findViewById(R.id.textbutton);
        mEditText = (EditText) layout.findViewById(R.id.postEditText);
        CardView mapCardView = (CardView) layout.findViewById(R.id.mapcardView);
        if (position == 0) {
            mViewText.setText(mContext.getResources().getString(R.string.help_message));
            mImageButton.setAlpha(Constants.sTransparencyLevelFade);
            mViewText.setAlpha(Constants.sTransparencyLevelFade);
            mTextButton.setAlpha(Constants.sTransparencyLevelFade);
            mBelowViewPagerText.setText(mContext.getResources().getString(R.string.expire_msg));
            mViewText.setOnClickListener(this);
            mTextButton.setOnClickListener(this);
            mImageButton.setOnClickListener(this);
        } else {
            mParentImageButton.setVisibility(View.GONE);
            mTextButton.setVisibility(View.GONE);
        }
        if (position == 4) {
            backgroundView.setAlpha(Constants.sTransparencyLevelBackground);
            if (mapCardView != null) {
                mapCardView.setBackgroundColor(Color.TRANSPARENT);
            }
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
        if (v.getId() == R.id.viewText) {
            mEditText.setVisibility(View.VISIBLE);
            mViewText.setVisibility(View.GONE);
        }
        mClickListener.onClick(v, position,mEditText.getText().toString());
    }
}
