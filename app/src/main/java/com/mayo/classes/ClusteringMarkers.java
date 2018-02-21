package com.mayo.classes;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.mayo.models.MarkerClusters;

/**
 * Created by Lakshmi on 21/02/18.
 */

public class ClusteringMarkers {
    private ClusterManager<MarkerClusters> mClusterManager;
    private GoogleMap mGoogleMap;
    private Context mContext;

    ClusteringMarkers(Context pContext, GoogleMap pGoogleMap) {
        mContext = pContext;
        mGoogleMap = pGoogleMap;
        mClusterManager = new ClusterManager<>(pContext, pGoogleMap);
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.cluster();
    }
}
