package com.mayo.classes;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
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
        for (int i = 0; i < mMapDataModel.size(); i++) {
            if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                mMapViewPagerAdapter.deleteItemFromArrayList(i);
                mMapDataModel.get(i).getCardLatlng().getMarker().remove();
                value = CommonUtility.getPoints(mContext) + 1;
                CommonUtility.setPoints(value, mContext);
                CommonUtility.setFakeCardThree(true, mContext);
            } else {
                setFakeCardSmallIcon(mMapDataModel.get(i).getCardLatlng());
            }

            if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                if (cardLatlng != null) {
                    setPostIconLarge(cardLatlng);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(cardLatlng.getLatLng()).zoom(Constants.sKeyCameraZoom).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    if (mContext != null)
                        ((MapActivity) mContext).getCurrentLocation();
                }
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
                            cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                            mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, cardLatlng);
                        }
                    }
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                        setPostIconSmall(mMapDataModel.get(i).getCardLatlng());
                    }
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                        setFakeCardSmallIcon(mMapDataModel.get(i).getCardLatlng());
                    }
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                        setFakeCardSmallIcon(mMapDataModel.get(i).getCardLatlng());
                    }
                }
            }
        }
    }

    public int getFakeCardTwo(boolean pScrollingCheck) {
        int value = -1;
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                    setPostIconSmall(mMapDataModel.get(i).getCardLatlng());
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                    if (CommonUtility.getFakeCardThree(mContext)) {
                        pScrollingCheck = false;
                    }
                    if (!pScrollingCheck) {
                        mMapViewPagerAdapter.deleteItemFromArrayList(i);
                        mMapDataModel.get(i).getCardLatlng().getMarker().remove();
                        value = CommonUtility.getPoints(mContext) + 1;
                        CommonUtility.setPoints(value, mContext);
                        CommonUtility.setFakeCardOne(true, mContext);
                        mViewPagerMap.setCurrentItem(1);
                    }
                    CardLatlng cardLatlng = mMapDataModel.get(i + 1).getCardLatlng();
                    if (mShownCardMarker != null) {
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, cardLatlng);
                    }
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                        animateCamera(cardLatlng);
                    }
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        if (cardLatlng.getMarker() != null) {
                            cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                            setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconSmall());
                        }
                    }
                }
            }
        }
        return value;
    }

    public int getFakeCardThree(boolean pScrollCheck) {
        int value = -1;
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                    setPostIconSmall(mMapDataModel.get(i).getCardLatlng());
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                        animateCamera(cardLatlng);
                    }
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    if (mShownCardMarker != null) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                        setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconSmall());
                    }
                }
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                    if (CommonUtility.getFakeCardTwo(mContext) && !pScrollCheck) {
                        mMapViewPagerAdapter.deleteItemFromArrayList(i);
                        mMapDataModel.get(i).getCardLatlng().getMarker().remove();
                        value = CommonUtility.getPoints(mContext) + 1;
                        CommonUtility.setPoints(value, mContext);
                        CommonUtility.setFakeCardOne(true, mContext);
                    } else {
                        if (mShownCardMarker != null) {
                            CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                            cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                            setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconSmall());
                        }
                    }
                }
            }
        }
        return value;
    }

    private void setCardLatlngMarker(CardLatlng pCardLatlng, BitmapDescriptor pBitmapDescriptior) {
        if (pCardLatlng.getMarker() != null) {
            pCardLatlng.getMarker().setIcon(pBitmapDescriptior);
        }
    }

    private void animateCamera(CardLatlng pCardLatlng) {
        mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, pCardLatlng);
    }

    private void setFakeCardSmallIcon(CardLatlng pCardLatlng) {
        if (pCardLatlng != null) {
            if (mShownCardMarker != null && pCardLatlng.getMarker() != null) {
                pCardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                setCardLatlngMarker(pCardLatlng, mShownCardMarker.getIconSmall());
            }
        }
    }

    public void setFakeCardLargeIcon(int pFakeCardIconLarge) {
        for (int i = 0; i < mMapDataModel.size(); i++) {
            CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
            if (cardLatlng != null) {
                if (mMapDataModel.get(i).getFakeCardPosition() == pFakeCardIconLarge) {
                    if (mShownCardMarker != null) {
                        cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                        if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                            setCardLatlngMarker(cardLatlng, mShownCardMarker.getBlueIconLarge());
                        } else {
                            setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconLarge());
                        }
                    }
                } else {
                    if (mShownCardMarker != null) {
                        if (cardLatlng.getMarker() != null) {
                            cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                            if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                                setCardLatlngMarker(cardLatlng, mShownCardMarker.getBlueIconSmall());
                            } else {
                                setCardLatlngMarker(cardLatlng, mShownCardMarker.getIconSmall());
                            }
                        }
                    }
                }
            }
        }
    }

    private void setPostIconLarge(CardLatlng pCardLatLng) {
        if (CommonUtility.getTaskApplied(mContext)) {
            if (pCardLatLng != null && pCardLatLng.getMarker() != null) {
                pCardLatLng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                setCardLatlngMarker(pCardLatLng, mShownCardMarker.getBlueIconLarge());
            }
        }
    }

    private void setPostIconSmall(CardLatlng pCardLatLng) {
        if (CommonUtility.getTaskApplied(mContext)) {
            if (pCardLatLng != null) {
                pCardLatLng.getMarker().setZIndex(Constants.sMarkerZIndexMinimum);
                setCardLatlngMarker(pCardLatLng, mShownCardMarker.getBlueIconSmall());
            }
        }
    }
}
