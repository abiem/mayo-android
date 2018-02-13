package com.mayo.classes;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.models.CardLatlng;
import com.mayo.models.MapDataModel;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 13/02/18.
 */

public class ViewPagerScroller {
    private Context mContext;
    private CustomViewPager mViewPagerMap;
    private ArrayList<MapDataModel> mMapDataModel;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private ShownCardMarker mShownCardMarker;
    private Location mCurrentLocationForCardMarker;
    private GoogleMap mGoogleMap;

    public ViewPagerScroller(Context pContext, CustomViewPager pCustomViewPager, ArrayList<MapDataModel> pMapDataModel,
                             MapViewPagerAdapter pViewPagerAdapter, ShownCardMarker pShownCardMarker, Location pLocation, GoogleMap pGoogleMap) {
        mContext = pContext;
        mViewPagerMap = pCustomViewPager;
        mMapDataModel = pMapDataModel;
        mMapViewPagerAdapter = pViewPagerAdapter;
        mShownCardMarker = pShownCardMarker;
        mCurrentLocationForCardMarker = pLocation;
        mGoogleMap = pGoogleMap;
    }

    public int getPostCard() {
        int value = -1;
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    mMapViewPagerAdapter.deleteItemFromArrayList(i);
                    mMapDataModel.get(i).getCardLatlng().getMarker().remove();
                    value = CommonUtility.getPoints(mContext) + 1;
                    CommonUtility.setPoints(value, mContext);
                    CommonUtility.setFakeCardThree(true, mContext);
                }
                getFakeCardIconSmall(mMapDataModel.get(i).getCardLatlng());
            }
        }
        return value;
    }

    public void getFakeCardOne() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardOne(mContext)) {
                for (int i = 0; i < mMapDataModel.size(); i++) {
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        if (mShownCardMarker != null) {
                            setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                            cardLatlng.getMarker().setZIndex(1.0f);
                            mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, cardLatlng);
                        }
                    }
                }
            }
        }
    }

    public int getFakeCardTwo() {
        int value = -1;
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                    mMapViewPagerAdapter.deleteItemFromArrayList(i);
                    CardLatlng cardLatlng = mMapDataModel.get(i + 1).getCardLatlng();
                    mMapDataModel.get(i).getCardLatlng().getMarker().remove();
                    value = CommonUtility.getPoints(mContext) + 1;
                    CommonUtility.setPoints(value, mContext);
                    CommonUtility.setFakeCardOne(true, mContext);
                    mViewPagerMap.setCurrentItem(1);
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, cardLatlng);
                    }
                    break;
                }
            }
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(1.0f);
                        animateCamera(cardLatlng);
                        break;
                    }
                }
            }
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(mMapDataModel.get(i).getCardLatlng(), mShownCardMarker.getIconSmall());
                        break;
                    }
                }
            }
        }
        return value;
    }

    public void getFakeCardThree() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(1.0f);
                        animateCamera(cardLatlng);
                        break;
                    }
                }
            }
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(mMapDataModel.get(i).getCardLatlng(), mShownCardMarker.getIconSmall());
                        break;
                    }
                }
            }
        }
    }

    private void setCardLatlngMarker(CardLatlng pCardLatlng, BitmapDescriptor pBitmapDescriptior) {
        pCardLatlng.getMarker().setIcon(pBitmapDescriptior);
    }

    private void animateCamera(CardLatlng pCardLatlng) {
        mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, pCardLatlng);
    }

    private void getFakeCardIconSmall(CardLatlng pCardLatlng) {
        if (pCardLatlng != null) {
            if (mShownCardMarker != null) {
                setCardLatlngMarker(pCardLatlng, mShownCardMarker.getIconSmall());
            }

        }
    }

    public void getFakeIconLargeCard(int pFakeCardIconLarge) {
        for (int i = 0; i < mMapDataModel.size(); i++) {
            CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
            if (cardLatlng != null) {
                if (mMapDataModel.get(i).getFakeCardPosition() == pFakeCardIconLarge) {
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(1.0f);
                    }
                } else {
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconSmall());
                    }
                }
            }
        }
    }

}
