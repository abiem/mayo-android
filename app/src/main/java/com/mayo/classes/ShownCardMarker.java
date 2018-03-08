package com.mayo.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;

import com.mayo.activities.MapActivity;
import com.mayo.models.CardLatlng;
import com.mayo.models.MapDataModel;
import com.mayo.models.MarkerTag;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lakshmi on 12/02/18.
 */

public class ShownCardMarker {
    private Context mContext;
    private ArrayList<MapDataModel> mMapDataModel;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private BitmapDescriptor iconLarge = BitmapDescriptorFactory.fromResource(R.drawable.green_task_icon_large);
    private BitmapDescriptor iconSmall = BitmapDescriptorFactory.fromResource(R.drawable.task_small_icons);
    private BitmapDescriptor blueIconLarge = BitmapDescriptorFactory.fromResource(R.drawable.blue_task_icon_large);
    private BitmapDescriptor blueIconSmall = BitmapDescriptorFactory.fromResource(R.drawable.blue_task_icon_small);

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
                        setGoogleMapPosition(mLocation, cardLatlng);
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
                        setGoogleMapPosition(mLocation, cardLatlng);
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
                        setGoogleMapPosition(mLocation, cardLatlng);
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

    private MarkerOptions getTaskCardMarker(boolean psetStateOfFakeMarker) {
        for (int i = 0; i < mMapDataModel.size(); i++) {
            if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                if (cardLatlng != null) {
                    if (psetStateOfFakeMarker) {
                        return new MarkerOptions().position(CommonUtility.getTaskLocation(mContext)).icon(blueIconSmall);
                    } else {
                        return new MarkerOptions().position(CommonUtility.getTaskLocation(mContext)).icon(blueIconLarge);
                    }
                }

            }
        }
        return null;
    }

    public BitmapDescriptor getIconLarge() {
        return iconLarge;
    }

    public BitmapDescriptor getBlueIconLarge() {
        return blueIconLarge;
    }

    public BitmapDescriptor getBlueIconSmall() {
        return blueIconSmall;
    }

    public BitmapDescriptor getIconSmall() {
        return iconSmall;
    }

    public void getCardMarkers() {
        MarkerOptions firstFakeCardMarkerOptions = getFirstFakeCardMarkers();
        boolean setStateOfFirstMarker = true, setStateOfSecondMarker = true, setStateOfFakeMarker = false;
        if (firstFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    Marker marker = mGoogleMap.addMarker(firstFakeCardMarkerOptions);
                    marker.setTag(Constants.CardType.FAKECARDONE.getValue());
                    marker.setZIndex(Constants.sMarkerZIndexMaximum);
                    cardLatlng.setMarker(marker);
                    setStateOfFirstMarker = false;
                    setStateOfFakeMarker = true;
                    break;
                }
            }
        }
        MarkerOptions secondFakeCardMarkerOptions = getSecondFakeCardMarkers(setStateOfFirstMarker);
        if (secondFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    Marker marker = mGoogleMap.addMarker(secondFakeCardMarkerOptions);
                    marker.setTag(Constants.CardType.FAKECARDTWO.getValue());
                    cardLatlng.setMarker(marker);
                    setStateOfSecondMarker = false;
                    setStateOfFakeMarker = true;
                    break;
                }
            }
        }
        MarkerOptions thirdFakeCardMarkerOptions = getThirdFakeCardMarkers(setStateOfSecondMarker);
        if (thirdFakeCardMarkerOptions != null) {
            for (int i = 0; i < mMapDataModel.size(); i++) {
                if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    Marker marker = mGoogleMap.addMarker(thirdFakeCardMarkerOptions);
                    marker.setTag(Constants.CardType.FAKECARDTHREE.getValue());
                    cardLatlng.setMarker(marker);
                    setStateOfFakeMarker = true;
                    break;
                }
            }
        }

        MarkerOptions taskCardMarker = getTaskCardMarker(setStateOfFakeMarker);
        if (taskCardMarker != null) {
            if (CommonUtility.getTaskApplied(mContext)) {
                for (int i = 0; i < mMapDataModel.size(); i++) {
                    if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                        Marker marker = mGoogleMap.addMarker(taskCardMarker);
                        marker.setTag(Constants.CardType.FAKECARDTHREE.getValue());
                    }
                }
            }
        }
    }

    public void getAnotherUsersLiveMarker() {
        for (int i = 0; i < mMapDataModel.size(); i++) {
            if (mMapDataModel.get(i).getFakeCardPosition() == Constants.CardType.DEFAULT.getValue()) {
                if (mMapDataModel.get(i).getTaskLatLng() != null && !mMapDataModel.get(i).getTaskLatLng().getTask().isCompleted()) {
                    CardLatlng cardLatlng = mMapDataModel.get(i).getCardLatlng();
                    if (cardLatlng.getMarker() != null) {
                        cardLatlng.getMarker().remove();
                    }
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(cardLatlng.getLatLng()).icon(iconSmall));
                    MarkerTag markerTag = new MarkerTag();
                    markerTag.setId(String.valueOf(Constants.CardType.DEFAULT.getValue()));
                    markerTag.setIdNew(String.valueOf(i));
                    marker.setTag(markerTag);
                    cardLatlng.setMarker(marker);
                }
            }
        }
    }

    public void setGoogleMapPosition(Location pCurrentLocationCardMarker, CardLatlng pCardLatlng) {
        LatLng latLng = new LatLng(pCurrentLocationCardMarker.getLatitude() + pCardLatlng.getLatLng().latitude,
                pCurrentLocationCardMarker.getLongitude() + pCardLatlng.getLatLng().longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(Constants.sKeyCameraZoom).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void setTaskMarker(Location pLocation) {
        LatLng latLng = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(blueIconLarge)
                .zIndex(Constants.sMarkerZIndexMaximum);
        Marker marker = mGoogleMap.addMarker(markerOptions);
        if (marker != null) {
            CardLatlng cardLatlng = new CardLatlng();
            cardLatlng.setLatLng(latLng);
            mMapDataModel.get(0).setCardLatlng(cardLatlng);
            mMapDataModel.get(0).getCardLatlng().setMarker(marker);
            marker.setTag(Constants.CardType.POST.getValue());
        }
    }

    public void setOtherUsersTaskMarker(MapDataModel pMapDataModel, int count) {
        MarkerOptions markerOptions = new MarkerOptions().position(pMapDataModel.getCardLatlng().getLatLng()).icon(iconSmall)
                .zIndex(Constants.sMarkerZIndexMinimum);
        Marker marker = mGoogleMap.addMarker(markerOptions);
        if (marker != null) {
            MarkerTag markerTag = new MarkerTag();
            markerTag.setId(String.valueOf(Constants.CardType.DEFAULT.getValue()));
            markerTag.setIdNew(String.valueOf(count));
            marker.setTag(markerTag);
            pMapDataModel.getCardLatlng().setMarker(marker);
        }
    }

    public void setMarkerTagsOnNewTaskFetch(ArrayList<MapDataModel> pMapDataModel, int count) {
        for (int i = count + 1; i < pMapDataModel.size(); i++) {
            Marker marker = pMapDataModel.get(i).getCardLatlng().getMarker();
            MarkerTag markerTag = new MarkerTag();
            markerTag.setId(String.valueOf(Constants.CardType.DEFAULT.getValue()));
            markerTag.setIdNew(String.valueOf(count + 1));
            //this is beacuse we have not store expire card marker
            if (marker != null) {
                marker.setTag(markerTag);
                pMapDataModel.get(i).getCardLatlng().getMarker().remove();
                pMapDataModel.get(i).getCardLatlng().setMarker(marker);
            }
        }
    }

    public void setTaskMarker(LatLng pLatlng) {
        MarkerOptions markerOptions;
        if (CommonUtility.getFakeCardShownOrNot(mContext)) {
            markerOptions = new MarkerOptions().position(pLatlng).icon(blueIconSmall)
                    .zIndex(Constants.sMarkerZIndexMinimum);
        } else {
            markerOptions = new MarkerOptions().position(pLatlng).icon(blueIconLarge)
                    .zIndex(Constants.sMarkerZIndexMaximum);
        }
        Marker marker = mGoogleMap.addMarker(markerOptions);
        if (marker != null) {
            CardLatlng cardLatlng = new CardLatlng();
            cardLatlng.setLatLng(pLatlng);
            mMapDataModel.get(0).setCardLatlng(cardLatlng);
            mMapDataModel.get(0).getCardLatlng().setMarker(marker);
            marker.setTag(Constants.CardType.POST.getValue());
        }
    }

    Marker setExpiryCardMarker(LatLng latLng) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.location_expired_card));
        BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .icon(currentLocationIcon).zIndex(Constants.sMarkerZIndexMaximum);
        Marker marker = mGoogleMap.addMarker(markerOptions);
        marker.setTag(Constants.CardType.DEFAULT.getValue());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(Constants.sKeyCameraZoom).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return marker;
    }
}
