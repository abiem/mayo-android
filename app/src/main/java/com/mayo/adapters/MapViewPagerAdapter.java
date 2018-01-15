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
import com.mayo.models.MapDataModel;

import java.util.ArrayList;


/**
 * Created by Lakshmikodali on 14/01/18.
 */

public class MapViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<MapDataModel> mMapDataModelArrayList;

    public MapViewPagerAdapter(Context pContext, ArrayList<MapDataModel> pMapDataModel) {
        mContext = pContext;
        mMapDataModelArrayList = pMapDataModel;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.map_viewpager_screen, collection, false);
        RelativeLayout backgroundView = (RelativeLayout) layout.findViewById(R.id.backgroundmapviewpager);
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
}
