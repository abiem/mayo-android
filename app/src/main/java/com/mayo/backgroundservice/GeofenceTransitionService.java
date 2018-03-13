package com.mayo.backgroundservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mayo.Utility.*;
import com.mayo.Utility.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.zzp.getErrorString;

/**
 * Created by Lakshmi on 12/03/18.
 */

public class GeofenceTransitionService extends IntentService {
    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTransitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }
        // Retrieve GeofenceTransition
        if (geofencingEvent != null) {
            int geoFenceTransition = geofencingEvent.getGeofenceTransition();

            switch (geoFenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    // Get the geofence that were triggered
                    List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                    String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
                    Intent lbcIntent = new Intent(Constants.BroadCastReceiver.sBroadCastName); //Send to any reciever listening for this
                    LocalBroadcastManager.getInstance(this).sendBroadcast(lbcIntent);
                    break;
            }

        }
    }

    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting ";
        return status + TextUtils.join(", ", triggeringGeofencesList);
    }
}
