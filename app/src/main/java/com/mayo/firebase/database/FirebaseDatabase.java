package com.mayo.firebase.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
import com.mayo.models.Channel;
import com.mayo.models.Message;
import com.mayo.models.Task;
import com.mayo.models.TaskLocations;
import com.mayo.models.TaskViews;
import com.mayo.models.UserId;
import com.mayo.models.UserMarker;
import com.mayo.models.Users;
import com.mayo.models.UsersLocations;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    private DatabaseReference mDatabaseReference;
    private GeoFire mGeoFire, mTaskGeoFire;
    private Context mContext;

    private static final String sInitDatabaseChild = "android";
    private static final String swriteNew_UpdateTask = "tasks";
    private static final String sTask_location = "tasks_locations";
    private static final String sTask_Views = "task_views";
    private static final String sChannel = "channels";
    private static final String sUserLocations = "users_locations";
    private HashMap<String, Task> mHashMap;

    public FirebaseDatabase(Context pContext) {
        mContext = pContext;
        mHashMap = new HashMap<>();
        //intialize database reference
        initDatabase();
    }

    private void initDatabase() {
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child(sInitDatabaseChild);
        // mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pTimeStamp).setValue(pTask);
    }

    public GeoFire setTaskLocationWithGeoFire() {
        return new GeoFire(mDatabaseReference.child(sTask_location));
    }

    public void setTaskViewsByUsers(String pTimeStamp, TaskViews pTaskView) {
        mDatabaseReference.child(sTask_Views).child(pTimeStamp).setValue(pTaskView);
    }

    public void writeNewChannel(String pTimeStamp, Channel pChannel, Message pMessage) {
        mDatabaseReference.child(sChannel).child(pTimeStamp).setValue(pChannel);
        mDatabaseReference.child(sChannel).child(pTimeStamp).child("messages").push().setValue(pMessage);
    }


    public void updateTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pTimeStamp).setValue(pTask);
    }

    public GeoFire locationUpdatesOfUserLocationWithGeoFire() {
        if (mGeoFire == null) {
            mGeoFire = new GeoFire(mDatabaseReference.child(sUserLocations));
        }
        return mGeoFire;
    }

    public GeoFire getTaskLocationWithGeoFire() {
        if (mTaskGeoFire == null) {
            mTaskGeoFire = new GeoFire(mDatabaseReference.child(sTask_location));
        }
        return mTaskGeoFire;
    }

    public void writeNewUser(Users pUser) {
        mDatabaseReference.child("users").setValue(pUser);
    }

    public Channel getChannel(String pSenderId) {
        UserId userId = new UserId();
        userId.setUser(pSenderId);

        Channel channel = new Channel();
        channel.setUserId(userId);
        return channel;
    }

    public Message setMessage(String pSenderId, String pMessage, String pColorIndex) {
        Message message = new Message();
        message.setColorIndex(pColorIndex);
        message.setDateCreated(CommonUtility.getLocalTime());
        message.setSenderId(pSenderId);
        message.setSenderName(Constants.sConstantEmptyString);
        message.setText(pMessage);
        return message;
    }

    public Task setTask(String pMessage, Context pContext, String pStartColor, String pEndColor) {
        Task task = new Task();
        task.setCreatedby(CommonUtility.getUserId(pContext));
        task.setTaskID(CommonUtility.convertLocalTimeToUTC());
        task.setHelpedBy(Constants.sConstantEmptyString);
        task.setTimeCreated(CommonUtility.getLocalTime()); //this is time when we create task
        task.setCompleted(false);
        task.setTaskDescription(pMessage);
        task.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        task.setUserMovedOutside(false);
        task.setRecentActivity(false);
        task.setStartColor(pStartColor);
        task.setEndColor(pEndColor);
        task.setCompleteType(Constants.sConstantEmptyString);
        return task;
    }

    public Task updateTaskOnFirebase(boolean pCompleteOrNot, String pCompleteType, Context pContext) {
        Task task = CommonUtility.getTaskData(pContext);
        Task updateTaskData = new Task();
        updateTaskData.setCreatedby(CommonUtility.getUserId(pContext));
        updateTaskData.setTaskID(task.getTaskID());
        updateTaskData.setHelpedBy(Constants.sConstantEmptyString);
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

    public void setUpdateTimeOfCurrentTask(String pKey) {
        mDatabaseReference.child("tasks").child(pKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Task task = dataSnapshot.getValue(Task.class);
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendarNewInstance = Calendar.getInstance();
                    Date saveUpdatedate, getFirebaseUpdateDate;
                    if (task != null) {
                        if (CommonUtility.getTaskApplied(mContext)) {
                            Task saveTaskData = CommonUtility.getTaskData(mContext);
                            saveUpdatedate = CommonUtility.convertStringToDateTime(saveTaskData.getTimeUpdated());
                            getFirebaseUpdateDate = CommonUtility.convertStringToDateTime(task.getTimeUpdated());
                            if (saveUpdatedate != null && getFirebaseUpdateDate != null &&
                                    getFirebaseUpdateDate.before(saveUpdatedate)) {
                                ((MapActivity) mContext).scheduleTaskTimer(0, task);
                                return;
                            }
                            calendar.setTime(getFirebaseUpdateDate);
                            calendar.add(Calendar.HOUR, 1);
                            if (saveUpdatedate != null && calendar.getTime().before(calendarNewInstance.getTime())) {
                                ((MapActivity) mContext).scheduleTaskTimer(0, task);
                                return;
                            }
                            long diff = calendar.getTimeInMillis() - calendarNewInstance.getTimeInMillis();
                            if (diff > Constants.sTaskExpiryTime) {
                                ((MapActivity) mContext).scheduleTaskTimer(Constants.sTaskExpiryTime, task);
                                return;
                            }
                            ((MapActivity) mContext).scheduleTaskTimer((int) diff, task);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * fetch live users from firebase and show only that users which is less than 6 minutes
     */
    public void getUsersCurrentTimeFromFirebase(final String pKey, final UsersLocations pUserLocations, final GoogleMap pGoogleMap) {
        mDatabaseReference.child("users").child(pKey).child("UpdatedAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserMarker userMarker = null;
                if (snapshot.getValue() != null) {
                    String updateTime = snapshot.getValue().toString();
                    Date taskUpdateTime = CommonUtility.convertStringToDateTime(updateTime);
                    Date currentTime = CommonUtility.getCurrentTime();
                    if (taskUpdateTime != null && currentTime != null) {
                        long diffTime = (currentTime.getTime() - taskUpdateTime.getTime()) / 1000;
                        if (diffTime < Constants.seconds) {
                            int timeToDisplayMarker = (int) (Constants.seconds - diffTime);
                            userMarker = new UserMarker();
                            userMarker.setStartTime(currentTime);
                            userMarker.setEndTime(CommonUtility.getEndTimeOfRealUsers(timeToDisplayMarker, currentTime));
                            userMarker.setLatLng(new LatLng(pUserLocations.getLatitude(), pUserLocations.getLongitude()));
                            userMarker.setMarker(pGoogleMap.addMarker(setUserMarker(userMarker)));
                            userMarker.setKey(pKey);
                        }
                    }
                }
                ((MapActivity) mContext).setUsersIntoList(pKey, userMarker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private MarkerOptions setUserMarker(UserMarker pUserMarker) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.location_fake_users_circle));
        BitmapDescriptor fakeLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        LatLng latLng = pUserMarker.getLatLng();
        return new MarkerOptions().position(latLng).icon(fakeLocationIcon);
    }


    public void getTaskFromFirebase(final TaskLocations pTaskLocations) {
        mDatabaseReference.child("tasks").child(pTaskLocations.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        if (mHashMap.containsKey(task.getTaskID())) {
                            ((MapActivity) mContext).updateTaskCardFromViewPager(task);
                        } else {
                            mHashMap.put(task.getTaskID(), task);
                            ((MapActivity) mContext).setListsOfFetchingTask(task, pTaskLocations);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
