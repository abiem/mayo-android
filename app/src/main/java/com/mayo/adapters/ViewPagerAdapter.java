package com.mayo.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.tutorial_screen, collection, false);
        Button tutorialButton = (Button) layout.findViewById(R.id.buttontutorial);
        TextView textView = (TextView) layout.findViewById(R.id.tutorialText);
        RelativeLayout backgroundView = (RelativeLayout) layout.findViewById(R.id.backgroundview);
        tutorialButton.setText(mTutorialModelArrayList.get(position).getButtonMessage());
        tutorialButton.setTag(String.valueOf(position));
        textView.setText(mTutorialModelArrayList.get(position).getTextMessage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            backgroundView.setBackground(mTutorialModelArrayList.get(position).getBackgroundView());
        } else {
            backgroundView.setBackgroundDrawable(mTutorialModelArrayList.get(position).getBackgroundView());
        }
        tutorialButton.setOnClickListener(this);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public int getCount() {
        return mTutorialModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void onClick(View v) {
        mButtonClickListener.onClick(v);
    }
}
