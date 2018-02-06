package com.mayo.firebase.database;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.mayo.models.Channel;
import com.mayo.models.Task;
import com.mayo.models.TaskViews;
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

    public void writeNewTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child("tasks").child(pTimeStamp).setValue(pTask);
    }

    public GeoFire setTaskLocationWithGeoFire() {
        return new GeoFire(mDatabaseReference.child("tasks_locations"));
    }

    public void setTaskViewsByUsers(String pTimeStamp, TaskViews pTaskView) {
        mDatabaseReference.child("task_views").child(pTimeStamp).setValue(pTaskView);
    }

    public void setNewChannel(String pTimeStamp, Channel pChannel) {
        mDatabaseReference.child("channels").child(pTimeStamp).setValue(pChannel);
    }

    public GeoFire locationUpdatesOfUserLocationWithGeoFire() {
        if (mGeoFire == null) {
            mGeoFire = new GeoFire(mDatabaseReference.child("users_locations"));
        }
        return mGeoFire;
    }

    public void writeNewUser(Users pUser) {
        mDatabaseReference.child("users").setValue(pUser);
    }
}
