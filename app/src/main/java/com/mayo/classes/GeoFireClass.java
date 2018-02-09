package com.mayo.classes;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
import com.mayo.firebase.database.FirebaseDatabase;


/**
 * Created by Lakshmi on 06/02/18.
 */

public class GeoFireClass {
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private Context mContext;

    public GeoFireClass(Context pContext) {
        mContext = pContext;
    }

    public void setGeoFire() {
        FirebaseDatabase firebaseDatabase = new FirebaseDatabase(mContext);
        mGeoFire = firebaseDatabase.locationUpdatesOfUserLocationWithGeoFire();
    }

    public void sendDataToFirebaseOfUserLocation(Location plocation) {
        mGeoFire.setLocation(CommonUtility.getUserId(mContext),
                new GeoLocation(plocation.getLatitude(), plocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        ((MapActivity) mContext).setGeoFireCompleteListener();
                    }
                });
    }

    public GeoQuery setGeoQuery(Location pLocation) {
        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(pLocation.getLatitude(),
                pLocation.getLongitude()), Constants.sKeyForMapRadiusInDouble);
        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                ((MapActivity) mContext).getNearByUsers(key,location);
            }

            @Override
            public void onKeyExited(String key) {
                ((MapActivity) mContext).moveMarkerOutsideFromCurrentLocation();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                ((MapActivity) mContext).createMarkerOfRealTimeUsers();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                ((MapActivity) mContext).onGeoQueryError(error);
            }
        });
        return mGeoQuery;
    }
}
