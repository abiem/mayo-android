package com.mayo.firebase.database;

import android.content.Context;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.models.Channel;
import com.mayo.models.Task;
import com.mayo.models.TaskViews;
import com.mayo.models.UserMarker;
import com.mayo.models.Users;
import com.mayo.models.UsersLocations;
import com.mayo.models.UsersTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    private DatabaseReference mDatabaseReference;
    private GeoFire mGeoFire;
    private Context mContext;

    public FirebaseDatabase(Context pContext) {
        mContext = pContext;
        //intialize database reference
        initDatabase();
    }

    private void initDatabase() {
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
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


    public void updateTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child("tasks").child(pTimeStamp).setValue(pTask);
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

    public Task setTask(String pMessage, Context pContext, String startColor, String endColor) {
        Task task = new Task();
        task.setCreatedby(CommonUtility.getUserId(pContext));
        task.setTaskID(CommonUtility.convertLocalTimeToUTC());
        task.setHelpedBy(Constants.sConstantString);
        task.setTimeCreated(CommonUtility.getLocalTime()); //this is time when we create task
        task.setCompleted(false);
        task.setTaskDescription(pMessage);
        task.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        task.setUserMovedOutside(false);
        task.setRecentActivity(false);
        task.setStartColor(startColor);
        task.setEndColor(endColor);
        task.setCompleteType("");
        return task;
    }

    public Task updateTaskOnFirebase(boolean pCompleteOrNot, String pCompleteType, Context pContext) {
        Task task = CommonUtility.getTaskData(pContext);
        Task updateTaskData = new Task();
        updateTaskData.setCreatedby(CommonUtility.getUserId(pContext));
        updateTaskData.setTaskID(task.getTaskID());
        updateTaskData.setHelpedBy(Constants.sConstantString);
        updateTaskData.setTimeCreated(task.getTimeCreated()); //this is time when we create task
        updateTaskData.setCompleted(pCompleteOrNot);
        updateTaskData.setTaskDescription(task.getTaskDescription());
        updateTaskData.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        updateTaskData.setUserMovedOutside(false);
        updateTaskData.setRecentActivity(false);
        updateTaskData.setStartColor(task.getStartColor());
        updateTaskData.setEndColor(task.getEndColor());
        updateTaskData.setCompleteType(pCompleteType);
        return updateTaskData;
    }

    public void getUsersTimeFromFirebase(String pKey, final HashSet<UsersLocations> pUserLocationsArray, final UsersLocations pUserLocations) {
        mDatabaseReference.child("users").child(pKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    UsersTime usersTime = snapshot.getValue(UsersTime.class);
                    if (usersTime != null) {
                        Date taskUpdateTime = CommonUtility.convertStringToDateTime(usersTime.getUpdatedAt());
                        Date currentTime = CommonUtility.getCurrentTime();
                        if (taskUpdateTime != null && currentTime != null) {
                            long diffTime = (currentTime.getTime() - taskUpdateTime.getTime()) / 1000;
                            if (diffTime > Constants.seconds) {
                                pUserLocationsArray.remove(pUserLocations);
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getTaskFromFirebase() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
