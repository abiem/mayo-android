package com.mayo.classes;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.mayo.R;
import com.mayo.Utility.Constants;
import com.mayo.backgroundservice.GeofenceTransitionService;

/**
 * Created by Lakshmi on 12/03/18.
 */

public class GeoFencing {
    Context mContext;
    PendingIntent mGeoFencePendingIntent;

    public GeoFencing(Context pContext) {
        mContext = pContext;
    }

    public Geofence createGeofences(double latitude, double longitude) {
        return new Geofence.Builder()
                .setRequestId(Constants.GeoFencing.GEOFENCE_REQ_ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT|Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(latitude, longitude, Constants.GeoFencing.sGeoFenceRadius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    // Create a Geofence Request
    public GeofencingRequest createGeofenceRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        if (mGeoFencePendingIntent != null)
            return mGeoFencePendingIntent;

        Intent intent = new Intent(mContext, GeofenceTransitionService.class);
        mGeoFencePendingIntent = PendingIntent.getService(
                mContext, Constants.GeoFencing.sGEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeoFencePendingIntent;
    }

    // Add the created GeofenceRequest to the device's monitoring list
    @SuppressLint("MissingPermission")
    public PendingResult<Status> addGeofence(GeofencingRequest pRequest, GoogleApiClient pGoogleApiClient) {
        return LocationServices.GeofencingApi.addGeofences(
                pGoogleApiClient,
                pRequest,
                createGeofencePendingIntent()
        );
    }

    public String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mResources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mResources
                        .getString(R.string.geofence_too_many_pending_intents);
            default:
                return mResources.getString(R.string.unknown_geofence_error);
        }
    }
}
