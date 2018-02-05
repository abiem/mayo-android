package com.mayo.firebase.database;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.mayo.models.Task;
import com.mayo.models.Users;


/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    private DatabaseReference mDatabaseReference;
    private GeoFire mGeoFire;

    public FirebaseDatabase() {
        //intialize database reference
        initDatabase();
    }

    private void initDatabase() {
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child("android");
    }

    // [START basic_write]
    public void writeNewTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child("tasks").child(pTimeStamp).setValue(pTask);
    }

    public GeoFire startLocationUpdatesWithGeoFire() {
        if (mGeoFire == null) {
            mGeoFire = new GeoFire(mDatabaseReference.child("users_locations"));
        }
        return mGeoFire;
    }


    public void writeNewUser(Users pUser) {
        mDatabaseReference.child("users").setValue(pUser);
    }
}
