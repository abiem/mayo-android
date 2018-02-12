package com.mayo.classes;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;

import com.mayo.models.CardLatlng;
import com.mayo.models.MapDataModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lakshmi on 12/02/18.
 */

public class ShownCardMarker {
    Context mContext;
    ArrayList<MapDataModel> mMapDataModel;
    Location mLocation;
    GoogleMap mGoogleMap;
    private BitmapDescriptor iconLarge = BitmapDescriptorFactory.fromResource(R.drawable.task_large_icons);
    private BitmapDescriptor iconSmall = BitmapDescriptorFactory.fromResource(R.drawable.task_small_icons);

    public ShownCardMarker(Context pContext, ArrayList<MapDataModel> pMapDataModel, Location pLocation, GoogleMap pGoogleMap) {
        mContext = pContext;
        mMapDataModel = pMapDataModel;
        mLocation = pLocation;
        mGoogleMap = pGoogleMap;
    }

    //array of fake user location
    static double[][] CardMarkerChoices = {
            {Constants.CardMarkerValues.sCardMarkerPositionMininum, Constants.CardMarkerValues.sCardMarkerPositionConstant},
            {Constants.CardMarkerValues.sCardMarkerPositionMaximum, Constants.CardMarkerValues.sCardMarkerPositionConstant},
            {Constants.CardMarkerValues.sCardMarkerPositionConstant, Constants.CardMarkerValues.sCardMarkerPositionMaximum},
            {Constants.CardMarkerValues.sCardMarkerPositionConstant, Constants.CardMarkerValues.sCardMarkerPositionMininum},
            {Constants.CardMarkerValues.sCardMarkerPositionConstantNew, Constants.CardMarkerValues.sCardMarkerPositionMininumNew},
            {Constants.CardMarkerValues.sCardMarkerPositionConstantNew, Constants.CardMarkerValues.sCardMarkerPositionMaximumNew},
            {Constants.CardMarkerValues.sCardMarkerPositionMaximumNew, Constants.CardMarkerValues.sCardMarkerPositionConstantNew},
            {Constants.CardMarkerValues.sCardMarkerPositionMininumNew, Constants.CardMarkerValues.sCardMarkerPositionConstantNew},
            {Constants.CardMarkerValues.sCardMarkerPositionConstant, Constants.CardMarkerValues.sCardMarkerPositionMininum},
    };

    static int generateRandomLocationOfCard() {
        //generate fake user location random
        Random random = new Random();
        return random.nextInt(CardMarkerChoices.length);
    }

    public MarkerOptions getFirstFakeCardMarkers() {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardOne(mContext)) {
                for (int i = 0; i < mMapDataModel.size(); i++) {
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        LatLng latLng = new LatLng(mLocation.getLatitude() + cardLatlng.getLatLng().latitude,
                                mLocation.getLongitude() + cardLatlng.getLatLng().longitude);
                        return new MarkerOptions().position(latLng).icon(iconLarge);
                    }
                }
            }
        }
        return null;
    }

    public MarkerOptions getSecondFakeCardMarkers(boolean isFirstMarkerVisible) {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardTwo(mContext)) {
                for (int i = 0; i < mMapDataModel.size(); i++) {
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        LatLng latLng = new LatLng(mLocation.getLatitude() + cardLatlng.getLatLng().latitude,
                                mLocation.getLongitude() + cardLatlng.getLatLng().longitude);
                        if (isFirstMarkerVisible) {
                            return new MarkerOptions().position(latLng).icon(iconLarge);
                        } else {
                            return new MarkerOptions().position(latLng).icon(iconSmall);
                        }
                    }
                }
            }
        }
        return null;
    }

    public MarkerOptions getThirdFakeCardMarkers(boolean isSecondMarkerVisible) {
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            if (!CommonUtility.getFakeCardThree(mContext)) {
                for (int i = 0; i < mMapDataModel.size(); i++) {
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                        CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                        LatLng latLng = new LatLng(mLocation.getLatitude() + cardLatlng.getLatLng().latitude,
                                mLocation.getLongitude() + cardLatlng.getLatLng().longitude);
                        if (isSecondMarkerVisible) {
                            return new MarkerOptions().position(latLng).icon(iconLarge);
                        } else {
                            return new MarkerOptions().position(latLng).icon(iconSmall);
                        }
                    }
                }
            }
        }
        return null;
    }

    public BitmapDescriptor getIconLarge() {
        return iconLarge;
    }

    public BitmapDescriptor getIconSmall() {
        return iconSmall;
    }

    public void getCardMarkers() {
        MarkerOptions firstFakeCardMarkerOptions = getFirstFakeCardMarkers();
        boolean setStateOfFirstMarker = true, setStateOfSecondMarker = true;
        if (firstFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    cardLatlng.setMarker(mGoogleMap.addMarker(firstFakeCardMarkerOptions));
                    setStateOfFirstMarker = false;
                }
            }
        }
        MarkerOptions secondFakeCardMarkerOptions = getSecondFakeCardMarkers(setStateOfFirstMarker);
        if (secondFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    cardLatlng.setMarker(mGoogleMap.addMarker(secondFakeCardMarkerOptions));
                    setStateOfSecondMarker = false;
                }
            }
        }
        MarkerOptions thirdFakeCardMarkerOptions = getThirdFakeCardMarkers(setStateOfSecondMarker);
        if (thirdFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    cardLatlng.setMarker(mGoogleMap.addMarker(thirdFakeCardMarkerOptions));
                }
            }
        }
    }
}
