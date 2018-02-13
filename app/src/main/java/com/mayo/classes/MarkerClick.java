package com.mayo.classes;

import android.content.Context;

import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.models.MapDataModel;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 13/02/18.
 */

public class MarkerClick {
    private Context mContext;
    private CustomViewPager mCustomMapView;
    private ArrayList<MapDataModel> mMapDataModels;

    public MarkerClick(Context pContext, CustomViewPager pCustomViewPager, ArrayList<MapDataModel> pMapDataModels) {
        mContext = pContext;
        mCustomMapView = pCustomViewPager;
        mMapDataModels = pMapDataModels;
    }

    public void getFirstFakeCard() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardOne(mContext)) {
                for (int i = 0; i < mMapDataModels.size(); i++) {
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                        mCustomMapView.setCurrentItem(i);
                        break;
                    }
                }
            }
        }
    }

    public void getSecondFakeCard() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardTwo(mContext)) {
                for (int i = 0; i < mMapDataModels.size(); i++) {
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                        mCustomMapView.setCurrentItem(i);
                        break;
                    }
                }
            }
        }
    }

    public void getThirdFakeCard() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardThree(mContext)) {
                for (int i = 0; i < mMapDataModels.size(); i++) {
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                        mCustomMapView.setCurrentItem(i);
                        break;
                    }
                }
            }
        }
    }
}
