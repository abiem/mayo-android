package com.mayo.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mayo.Classes.enums.CustomPagerEnum;
import com.mayo.R;
import com.mayo.interfaces.ButtonClickListener;
import com.mayo.models.TutorialModel;

import java.util.ArrayList;


/**
 * Created by Lakshmikodali on 08/01/18.
 */

public class ViewPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    private ButtonClickListener mButtonClickListener;
    private ArrayList<TutorialModel> mTutorialModelArrayList;

    public ViewPagerAdapter(Context pContext, ButtonClickListener pButtonClickListener, ArrayList<TutorialModel> pTutorialModelList) {
        mContext = pContext;
        mButtonClickListener = pButtonClickListener;
        mTutorialModelArrayList = pTutorialModelList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
        Button mFirstTutorial = (Button) layout.findViewById(R.id.buttontutorialfirst);
        Button mSecondTutorial = (Button) layout.findViewById(R.id.buttontutorialsecond);
        Button mThirdTutorial = (Button) layout.findViewById(R.id.buttontutorialthird);
        Button mFourthTutorial = (Button) layout.findViewById(R.id.buttontutorialfourth);
        if (mFirstTutorial != null) {
            mFirstTutorial.setOnClickListener(this);
        }
        if (mSecondTutorial != null) {
            mSecondTutorial.setOnClickListener(this);
        }
        if (mThirdTutorial != null) {
            mThirdTutorial.setOnClickListener(this);
        }
        if (mFourthTutorial != null) {
            mFourthTutorial.setOnClickListener(this);
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
        return CustomPagerEnum.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

    @Override
    public void onClick(View v) {
        mButtonClickListener.onClick(v);
    }
}
