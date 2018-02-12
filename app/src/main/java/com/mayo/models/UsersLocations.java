package com.mayo.models;

import com.firebase.geofire.GeoLocation;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class UsersLocations {
    private String key;
    private double latitude;
    private double longitude;

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
