package com.mayo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Lakshmi on 21/02/18.
 */

public class MarkerClusters implements ClusterItem {
    Marker marker;
    LatLng latLng;
    @Override
    public LatLng getPosition() {
        return latLng;
    }
}
