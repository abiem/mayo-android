package com.mayo.models;

import com.firebase.geofire.GeoLocation;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class UsersLocations {
    String key;
    double latitude;
    double longitude;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
