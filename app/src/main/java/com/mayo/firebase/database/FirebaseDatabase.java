package com.mayo.firebase.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mayo.Notifications.PushNotificationManager;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.ChatActivity;
import com.mayo.activities.MapActivity;
import com.mayo.adapters.ChatListAdapter;
import com.mayo.models.Location;
import com.mayo.models.Message;
import com.mayo.models.ScoreDetail;
import com.mayo.models.Task;
import com.mayo.models.TaskCreated;
import com.mayo.models.TaskLocations;
import com.mayo.models.TaskParticipated;
import com.mayo.models.TaskViews;
import com.mayo.models.UserMarker;
import com.mayo.models.UserData;
import com.mayo.models.UsersLocations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    private DatabaseReference mDatabaseReference, mAndroidDatabaseReference;
    private GeoFire mGeoFire, mTaskGeoFire;
    private Context mContext;

    private static final String sInitDatabaseChild = "android";
    private static final String swriteNew_UpdateTask = "tasks";
    private static final String sTask_location = "tasks_locations";
    private static final String sTask_Views = "task_views";
    private static final String sChannel = "channels";
    private static final String sUserLocations = "users_locations";
    private HashMap<String, Task> mHashMap;
    private HashMap mMessageHashMap, mTaskParticpatedHashMap, mGetAllUsers;
    public int currentUserColorIndex = -1;
    private boolean currentUserIsInConversation = false;
    private int usersCountAndNewColorIndex;
    private ChildEventListener mMessagelistener, mMessagesGetReferences;
    private ValueEventListener mPointsListener;
    private boolean sendMessageFromLocalDevice = false;
    private HashMap locationHashMap;
    private int locationHashMapValue = 0;
    private PushNotificationManager mPushNotificationManager;
    private boolean isThanksDialogAnimationRun = false;
    private String channelTopicMessage;


    public FirebaseDatabase(Context pContext) {
        mContext = pContext;
        mHashMap = new HashMap<>();
        mMessageHashMap = new HashMap();
        locationHashMap = new HashMap<>();
        mTaskParticpatedHashMap = new HashMap();
        mGetAllUsers = new HashMap();
        //intialize database reference
        initDatabase();
        initAndroidDatabaseReference();
        getInstancePushNotificationManager();
    }

    private void initAndroidDatabaseReference() {
        mAndroidDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child(sInitDatabaseChild);
    }

    private void initDatabase() {
        // mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child(sInitDatabaseChild);
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewTask(String pTimeStamp, Task pTask) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pTimeStamp).setValue(pTask);
    }

    public GeoFire setTaskLocationWithGeoFire() {
        return new GeoFire(mDatabaseReference.child(sTask_location));
    }

    public void setTaskViewsByUsers(final String pTimeStamp) {
        mDatabaseReference.child(sTask_Views).child(pTimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TaskViews taskViews = null;
                int count = 0;
                boolean isCountAdded = true;
                if (dataSnapshot.getValue() != null) {
                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    ArrayList arrayListTaskView = new ArrayList();
                    if (hashMap.containsKey("users")) {
                        ArrayList arrayList = (ArrayList) hashMap.get("users");
                        if (arrayList != null) {
                            count = arrayList.size();
                            for (int i = 0; i < arrayList.size(); i++) {
                                arrayListTaskView.add(arrayList.get(i));
                                if (arrayList.get(i).equals(CommonUtility.getUserId(mContext))) {
                                    isCountAdded = false;
                                }
                            }
                        }
                    }
                    if (isCountAdded) {
                        taskViews = new TaskViews();
                        taskViews.setCount(count + 1);
                        arrayListTaskView.add(CommonUtility.getUserId(mContext));
                        taskViews.setUsers(arrayListTaskView);
                    }
                } else {
                    taskViews = setTaskViewByUser(1);
                }
                if (taskViews != null)
                    mDatabaseReference.child(sTask_Views).child(pTimeStamp).setValue(taskViews);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private TaskViews setTaskViewByUser(int count) {
        TaskViews taskViews = new TaskViews();
        taskViews.setCount(count);
        ArrayList arrayList = new ArrayList();
        arrayList.add(CommonUtility.getUserId(mContext));
        taskViews.setUsers(arrayList);
        return taskViews;
    }

    private void writeNewChannel(String pTimeStamp, Message pMessage) {
        mDatabaseReference.child(sChannel).child(pTimeStamp).child("users").child(CommonUtility.getUserId(mContext))
                .setValue(Integer.parseInt(pMessage.getColorIndex()));
        mDatabaseReference.child(sChannel).child(pTimeStamp).child("messages").push().setValue(pMessage);
    }

    public void writeNewChannelForCurrentTask(String pTimeStamp) {
        mDatabaseReference.child(sChannel).child(pTimeStamp).child("users").child(CommonUtility.getUserId(mContext))
                .setValue(0);
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

    public void writeNewUserData(final String pUserId, final String pUpdateTime, final String pDeviceToken, final boolean pDemoTaskShownTrue, final android.location.Location pLocation) {
        mDatabaseReference.child("users").child(pUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                    UserData userData = new UserData();
                    if (hashMapUserData.containsKey("taskCreated")) {
                        HashMap hashMapTaskCreated = (HashMap) hashMapUserData.get("taskCreated");
                        TaskCreated taskCreated = new TaskCreated();
                        taskCreated.setCount(Integer.parseInt(hashMapTaskCreated.get("count").toString()));
                        ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskCreated.get("tasks");
                        HashMap hashMap = new HashMap();
                        for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                            hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                        }
                        taskCreated.setTasks(hashMap);
                        userData.setTaskCreated(taskCreated);
                    }
                    if (hashMapUserData.containsKey("score")) {
                        userData.setScore(Integer.parseInt(hashMapUserData.get("score").toString()));
                    }
                    if (hashMapUserData.containsKey("scoreDetail")) {
                        try {
                            ArrayList arrayListScoreDetail = (ArrayList) hashMapUserData.get("scoreDetail");
                            ArrayList<ScoreDetail> arrayListNewScoreDetail = new ArrayList<>();
                            for (int i = 0; i < arrayListScoreDetail.size(); i++) {
                                HashMap hashMap = (HashMap) arrayListScoreDetail.get(i);
                                ScoreDetail scoreDetail = new ScoreDetail();
                                if (hashMap.containsKey("createdDate")) {
                                    scoreDetail.setCreatedDate(hashMap.get("createdDate").toString());
                                }
                                if (hashMap.containsKey("taskID")) {
                                    scoreDetail.setTaskID(hashMap.get("taskID").toString());
                                }
                                if (hashMap.containsKey("points")) {
                                    scoreDetail.setPoints(Integer.parseInt(hashMap.get("points").toString()));
                                }
                                arrayListNewScoreDetail.add(scoreDetail);
                            }
                            userData.setScoreDetail(arrayListNewScoreDetail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (hashMapUserData.containsKey("taskParticipated")) {
                        HashMap hashMapTaskParticipated = (HashMap) hashMapUserData.get("taskParticipated");
                        TaskParticipated taskParticipated = new TaskParticipated();
                        taskParticipated.setCount(Integer.parseInt(hashMapTaskParticipated.get("count").toString()));
                        ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskParticipated.get("tasks");
                        HashMap hashMap = new HashMap();
                        for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                            hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                        }
                        taskParticipated.setTasks(hashMap);
                        userData.setTaskParticipated(taskParticipated);
                    }
                    if (hashMapUserData.containsKey("friends")) {
                        try {
                            ArrayList arrayList = (ArrayList) hashMapUserData.get("friends");
                            HashMap<String, String> hashMapFriendList = new HashMap<>();
                            for (int i = 0; i < arrayList.size(); i++) {
                                hashMapFriendList.put(String.valueOf(i), arrayList.get(i).toString());
                            }
                            userData.setFriends(hashMapFriendList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (hashMapUserData.containsKey("location")) {
                        try {
                            ArrayList arrayList = (ArrayList) hashMapUserData.get("location");
                            if (arrayList != null && arrayList.size() > 0) {
                                for (int i = 0; i < arrayList.size(); i++) {
                                    Location location = new Location();
                                    HashMap hashMap = (HashMap) arrayList.get(i);
                                    if (hashMap.containsKey("lat")) {
                                        location.setLat(Double.parseDouble(hashMap.get("lat").toString()));
                                    }
                                    if (hashMap.containsKey("long")) {
                                        location.setLong(Double.parseDouble(hashMap.get("long").toString()));
                                    }
                                    if (hashMap.containsKey("updatedAt")) {
                                        location.setUpdatedAt(hashMap.get("updatedAt").toString());
                                    }
                                    locationHashMap.put(String.valueOf(locationHashMapValue), location);
                                    locationHashMapValue++;
                                }
                                if (locationHashMapValue > 4) {
                                    locationHashMapValue = 0;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    userData.setDeviceToken(pDeviceToken);
                    userData.setUpdatedAt(pUpdateTime);
                    userData.setDemoTaskShown(pDemoTaskShownTrue);
                    Location location = new Location();
                    location.setLat(pLocation.getLatitude());
                    location.setLong(pLocation.getLongitude());
                    location.setUpdatedAt(CommonUtility.getLocalTime());
                    locationHashMap.put(String.valueOf(locationHashMapValue), location);
                    userData.setLocation(locationHashMap);
                    mDatabaseReference.child("users").child(pUserId).setValue(userData);
                    locationHashMapValue++;
                } else {
                    setUserData(pUserId, pUpdateTime, pDeviceToken, pDemoTaskShownTrue, pLocation);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private UserData setUserData(final String pUserId, final String pUpdateTime, final String pDeviceToken, final boolean pDemoTaskShownTrue, final android.location.Location pLocation) {
        UserData userData = new UserData();
        userData.setDeviceToken(pDeviceToken);
        userData.setUpdatedAt(pUpdateTime);
        userData.setDemoTaskShown(pDemoTaskShownTrue);
        Location location = new Location();
        location.setLat(pLocation.getLatitude());
        location.setLong(pLocation.getLongitude());
        location.setUpdatedAt(CommonUtility.getLocalTime());
        locationHashMap.put(String.valueOf(locationHashMapValue), location);
        userData.setLocation(locationHashMap);
        mDatabaseReference.child("users").child(pUserId).setValue(userData);
        locationHashMapValue++;
        return userData;
    }

    public void getUserIdsFromFirebase() {
        mDatabaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGetAllUsers.clear();
                if (dataSnapshot.getValue() != null) {
                    mGetAllUsers = (HashMap) dataSnapshot.getValue();
                    Log.e("Get All users", "" + mGetAllUsers.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getAllMessagesOfTaskEnteredByUser(String pTimeStamp, Context pContext) {
        mMessagesGetReferences = mDatabaseReference.child(sChannel).child(pTimeStamp).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        HashMap messageHashList = (HashMap) dataSnapshot.getValue();
                        if (messageHashList != null) {
                            Message message = new Message();
                            message.setColorIndex(messageHashList.get("colorIndex").toString());
                            message.setDateCreated(messageHashList.get("dateCreated").toString());
                            message.setSenderId(messageHashList.get("senderId").toString());
                            message.setSenderName(messageHashList.get("senderName").toString());
                            if (message.getSenderId().equals(CommonUtility.getUserId(mContext))) {
                                message.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.yes);
                                message.setUserType(Constants.UserType.OTHER);
                            } else {
                                message.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.no);
                                message.setUserType(Constants.UserType.SELF);
                            }
                            message.setText(messageHashList.get("text").toString());
                            mMessageHashMap.put(message.getSenderId(), message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeGetAllMessagesListener() {
        if (mMessagesGetReferences != null) {
            mDatabaseReference.removeEventListener(mMessagesGetReferences);
        }
    }

    public void writeNewUserLocation(final String pUserId, final android.location.Location pLocation) {
        mDatabaseReference.child("users").child(pUserId).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getChildrenCount() > 4 && locationHashMapValue == 5) {
                        locationHashMapValue = 0;
                    }
                    Location location = new Location();
                    location.setLat(pLocation.getLatitude());
                    location.setLong(pLocation.getLongitude());
                    location.setUpdatedAt(CommonUtility.getLocalTime());
                    locationHashMap.put(String.valueOf(locationHashMapValue), location);
                    mDatabaseReference.child("users").child(pUserId).child("location").updateChildren(locationHashMap);
                    locationHashMapValue++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void writeTaskParticipated(final String pUserId, final String pTaskId) {
        mDatabaseReference.child("users").child(pUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                    UserData userData = new UserData();
                    if (hashMapUserData != null) {
                        if (hashMapUserData.containsKey("demoTaskShown")) {
                            userData.setDemoTaskShown(Boolean.parseBoolean(hashMapUserData.get("demoTaskShown").toString()));
                        }
                        if (hashMapUserData.containsKey("updatedAt")) {
                            userData.setUpdatedAt(hashMapUserData.get("updatedAt").toString());
                        }
                        if (hashMapUserData.containsKey("deviceToken")) {
                            userData.setDeviceToken(hashMapUserData.get("deviceToken").toString());
                        }
                        if (hashMapUserData.containsKey("score")) {
                            userData.setScore(Integer.parseInt(hashMapUserData.get("score").toString()));
                        }
                        if (hashMapUserData.containsKey("location")) {
                            ArrayList arrayListLocation = (ArrayList) hashMapUserData.get("location");
                            HashMap<String, Location> hashMapArrayLocation = new HashMap<>();
                            for (int i = 0; i < arrayListLocation.size(); i++) {
                                HashMap hashMap = (HashMap) arrayListLocation.get(i);
                                Location location = new Location();
                                location.setLat(Double.parseDouble(hashMap.get("lat").toString()));
                                location.setLong(Double.parseDouble(hashMap.get("long").toString()));
                                location.setUpdatedAt(hashMap.get("updatedAt").toString());
                                hashMapArrayLocation.put(String.valueOf(i), location);
                            }
                            userData.setLocation(hashMapArrayLocation);
                        }
                        if (hashMapUserData.containsKey("taskParticipated")) {
                            boolean isNewTaskParticipated = true;
                            HashMap hashMapTaskParticipated = (HashMap) hashMapUserData.get("taskParticipated");
                            TaskParticipated taskParticipated = new TaskParticipated();
                            taskParticipated.setCount(Integer.parseInt(hashMapTaskParticipated.get("count").toString()) + 1);
                            ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskParticipated.get("tasks");
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                                hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                                if (arraylistTasksFetch.get(i).equals(pTaskId)) {
                                    isNewTaskParticipated = false;
                                    taskParticipated.setCount(Integer.parseInt(hashMapTaskParticipated.get("count").toString()));
                                }
                            }
                            if (isNewTaskParticipated) {
                                hashMap.put(String.valueOf(arraylistTasksFetch.size()), pTaskId);
                            }
                            taskParticipated.setTasks(hashMap);
                            userData.setTaskParticipated(taskParticipated);
                        } else {
                            TaskParticipated taskParticipated = new TaskParticipated();
                            taskParticipated.setCount(1);
                            HashMap hashMap = new HashMap();
                            hashMap.put(String.valueOf(0), pTaskId);
                            taskParticipated.setTasks(hashMap);
                            userData.setTaskParticipated(taskParticipated);
                        }
                        if (hashMapUserData.containsKey("scoreDetail")) {
                            try {
                                ArrayList arrayListScoreDetail = (ArrayList) hashMapUserData.get("scoreDetail");
                                ArrayList<ScoreDetail> arrayListNewScoreDetail = new ArrayList<>();
                                for (int i = 0; i < arrayListScoreDetail.size(); i++) {
                                    HashMap hashMap = (HashMap) arrayListScoreDetail.get(i);
                                    ScoreDetail scoreDetail = new ScoreDetail();
                                    if (hashMap.containsKey("createdDate")) {
                                        scoreDetail.setCreatedDate(hashMap.get("createdDate").toString());
                                    }
                                    if (hashMap.containsKey("taskID")) {
                                        scoreDetail.setTaskID(hashMap.get("taskID").toString());
                                    }
                                    if (hashMap.containsKey("points")) {
                                        scoreDetail.setPoints(Integer.parseInt(hashMap.get("points").toString()));
                                    }
                                    arrayListNewScoreDetail.add(scoreDetail);
                                }
                                userData.setScoreDetail(arrayListNewScoreDetail);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (hashMapUserData.containsKey("friends")) {
                            try {
                                ArrayList arrayList = (ArrayList) hashMapUserData.get("friends");
                                HashMap<String, String> hashMapFriendList = new HashMap<>();
                                for (int i = 0; i < arrayList.size(); i++) {
                                    hashMapFriendList.put(String.valueOf(i), arrayList.get(i).toString());
                                }
                                userData.setFriends(hashMapFriendList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (hashMapUserData.containsKey("taskCreated")) {
                            HashMap hashMapTaskCreated = (HashMap) hashMapUserData.get("taskCreated");
                            TaskCreated taskCreated = new TaskCreated();
                            taskCreated.setCount(Integer.parseInt(hashMapTaskCreated.get("count").toString()));
                            ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskCreated.get("tasks");
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                                hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                            }
                            taskCreated.setTasks(hashMap);
                            userData.setTaskCreated(taskCreated);

                        }
                        mDatabaseReference.child("users").child(pUserId).setValue(userData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void writeTaskCreatedInUserNode(final String pUserId, final String pTaskId) {
        mDatabaseReference.child("users").child(pUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                    UserData userData = new UserData();
                    if (hashMapUserData != null) {
                        if (hashMapUserData.containsKey("demoTaskShown")) {
                            userData.setDemoTaskShown(Boolean.parseBoolean(hashMapUserData.get("demoTaskShown").toString()));
                        }
                        if (hashMapUserData.containsKey("updatedAt")) {
                            userData.setUpdatedAt(hashMapUserData.get("updatedAt").toString());
                        }
                        if (hashMapUserData.containsKey("deviceToken")) {
                            userData.setDeviceToken(hashMapUserData.get("deviceToken").toString());
                        }
                        if (hashMapUserData.containsKey("score")) {
                            userData.setScore(Integer.parseInt(hashMapUserData.get("score").toString()));
                        }
                        if (hashMapUserData.containsKey("location")) {
                            ArrayList arrayListLocation = (ArrayList) hashMapUserData.get("location");
                            HashMap<String, Location> hashMapArrayLocation = new HashMap<>();
                            for (int i = 0; i < arrayListLocation.size(); i++) {
                                HashMap hashMap = (HashMap) arrayListLocation.get(i);
                                Location location = new Location();
                                location.setLat(Double.parseDouble(hashMap.get("lat").toString()));
                                location.setLong(Double.parseDouble(hashMap.get("long").toString()));
                                location.setUpdatedAt(hashMap.get("updatedAt").toString());
                                hashMapArrayLocation.put(String.valueOf(i), location);
                            }
                            userData.setLocation(hashMapArrayLocation);
                        }
                        if (hashMapUserData.containsKey("taskParticipated")) {
                            HashMap hashMapTaskParticipated = (HashMap) hashMapUserData.get("taskParticipated");
                            TaskParticipated taskParticipated = new TaskParticipated();
                            taskParticipated.setCount(Integer.parseInt(hashMapTaskParticipated.get("count").toString()));
                            ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskParticipated.get("tasks");
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                                hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                            }
                            taskParticipated.setTasks(hashMap);
                            userData.setTaskParticipated(taskParticipated);
                        }
                        if (hashMapUserData.containsKey("scoreDetail")) {
                            try {
                                ArrayList arrayListScoreDetail = (ArrayList) hashMapUserData.get("scoreDetail");
                                ArrayList<ScoreDetail> arrayListNewScoreDetail = new ArrayList<>();
                                for (int i = 0; i < arrayListScoreDetail.size(); i++) {
                                    HashMap hashMap = (HashMap) arrayListScoreDetail.get(i);
                                    ScoreDetail scoreDetail = new ScoreDetail();
                                    if (hashMap.containsKey("createdDate")) {
                                        scoreDetail.setCreatedDate(hashMap.get("createdDate").toString());
                                    }
                                    if (hashMap.containsKey("taskID")) {
                                        scoreDetail.setTaskID(hashMap.get("taskID").toString());
                                    }
                                    if (hashMap.containsKey("points")) {
                                        scoreDetail.setPoints(Integer.parseInt(hashMap.get("points").toString()));
                                    }
                                    arrayListNewScoreDetail.add(scoreDetail);
                                }
                                userData.setScoreDetail(arrayListNewScoreDetail);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (hashMapUserData.containsKey("friends")) {
                            try {
                                ArrayList arrayList = (ArrayList) hashMapUserData.get("friends");
                                HashMap<String, String> hashMapFriendList = new HashMap<>();
                                for (int i = 0; i < arrayList.size(); i++) {
                                    hashMapFriendList.put(String.valueOf(i), arrayList.get(i).toString());
                                }
                                userData.setFriends(hashMapFriendList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (hashMapUserData.containsKey("taskCreated")) {
                            boolean isNewTaskParticipated = true;
                            HashMap hashMapTaskCreated = (HashMap) hashMapUserData.get("taskCreated");
                            TaskCreated taskCreated = new TaskCreated();
                            taskCreated.setCount(Integer.parseInt(hashMapTaskCreated.get("count").toString()) + 1);
                            ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskCreated.get("tasks");
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < arraylistTasksFetch.size(); i++) {
                                hashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                                if (arraylistTasksFetch.get(i).equals(pTaskId)) {
                                    isNewTaskParticipated = false;
                                    taskCreated.setCount(Integer.parseInt(hashMapTaskCreated.get("count").toString()));
                                }
                            }
                            if (isNewTaskParticipated) {
                                hashMap.put(String.valueOf(arraylistTasksFetch.size()), pTaskId);
                            }
                            taskCreated.setTasks(hashMap);
                            userData.setTaskCreated(taskCreated);

                        } else {
                            TaskCreated taskCreated = new TaskCreated();
                            taskCreated.setCount(1);
                            HashMap hashMap = new HashMap();
                            hashMap.put(String.valueOf(0), pTaskId);
                            taskCreated.setTasks(hashMap);
                            userData.setTaskCreated(taskCreated);
                        }
                        mDatabaseReference.child("users").child(pUserId).setValue(userData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setMessage(final Context pContext, final String pSenderId, final String pMessage, final String pTimeStamp, String pTaskDescription) {
        sendMessageFromLocalDevice = true;
        if (currentUserColorIndex == -1) {
            mDatabaseReference.child(sChannel).child(pTimeStamp).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        HashMap usersValue = (HashMap) dataSnapshot.getValue();
                        if (usersValue != null) {
                            for (Object o : usersValue.entrySet()) {
                                Map.Entry pair = (Map.Entry) o;
                                if (pair.getKey().equals(CommonUtility.getUserId(mContext))) {
                                    currentUserColorIndex = Integer.parseInt(pair.getValue().toString());
                                    currentUserIsInConversation = true;
                                    break;
                                }
                            }

                            // if the user is not in the conversation
                            if (!currentUserIsInConversation) {
                                usersCountAndNewColorIndex = usersValue.size();
                                //save the index
                                currentUserColorIndex = usersCountAndNewColorIndex;
                            }
                            Message message = setMessageData(pSenderId, pMessage);
                            writeNewChannel(pTimeStamp, message);
                        }
                        ((ChatActivity) pContext).sendMessagesToUser(String.valueOf(currentUserColorIndex));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Message message = setMessageData(pSenderId, pMessage);
            writeNewChannel(pTimeStamp, message);
            ((ChatActivity) pContext).sendMessagesToUser(String.valueOf(currentUserColorIndex));
        }
        sendPushNotificationToTopic(pTimeStamp, pTaskDescription);
    }

    private Message setMessageData(String pSenderId, String pMessage) {
        Message message = new Message();
        message.setColorIndex(String.valueOf(currentUserColorIndex));
        message.setDateCreated(CommonUtility.getLocalTime());
        message.setSenderId(pSenderId);
        message.setSenderName(Constants.sConstantEmptyString);
        message.setText(pMessage);
        return message;
    }

    public void getMessagesFromFirebase(String pTimeStamp, final ChatListAdapter pChatAdapter, final ArrayList<Message> pMessageList, final RecyclerView pRecyclerView) {
        mMessagelistener = mDatabaseReference.child(sChannel).child(pTimeStamp).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    HashMap messageHashList = (HashMap) dataSnapshot.getValue();
                    if (messageHashList != null) {
                        try {
                            Message message = new Message();
                            message.setColorIndex(messageHashList.get("colorIndex").toString());
                            message.setDateCreated(messageHashList.get("dateCreated").toString());
                            message.setSenderId(messageHashList.get("senderId").toString());
                            message.setSenderName(messageHashList.get("senderName").toString());
                            if (message.getSenderId().equals(CommonUtility.getUserId(mContext))) {
                                message.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.yes);
                                message.setUserType(Constants.UserType.OTHER);
                            } else {
                                message.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.no);
                                message.setUserType(Constants.UserType.SELF);
                            }
                            message.setText(messageHashList.get("text").toString());
                            if (!sendMessageFromLocalDevice) {
                                pMessageList.add(message);
                            }
                            sendMessageFromLocalDevice = false;
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        //  }
                        if (pChatAdapter != null) {
                            pChatAdapter.notifyDataSetChanged();
                            pRecyclerView.scrollToPosition(pChatAdapter.getItemCount() - 1);
                        }

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeMessageListener() {
        if (mMessagelistener != null) {
            mDatabaseReference.removeEventListener(mMessagelistener);
        }
    }

    public Task setTask(String pMessage, Context pContext, String pStartColor, String pEndColor) {
        Task task = new Task();
        task.setCreatedby(CommonUtility.getUserId(pContext));
        task.setTaskID(CommonUtility.convertLocalTimeToUTC());
        task.setHelpedBy(new ArrayList<String>());
        task.setTimeCreated(CommonUtility.getLocalTime()); //this is time when we create task
        task.setCompleted(false);
        task.setTaskDescription(pMessage);
        task.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        task.setUserMovedOutside(false);
        task.setRecentActivity(false);
        task.setStartColor(pEndColor);
        task.setEndColor(pStartColor);
        task.setCompleteType(Constants.sConstantEmptyString);
        return task;
    }

    public Task updateTaskOnFirebase(boolean pCompleteOrNot, String pCompleteType, Context pContext, boolean pUserMoveOutside, boolean pRecentActivity, ArrayList<String> pArrayList) {
        Task task = CommonUtility.getTaskData(pContext);
        Task updateTaskData = new Task();
        updateTaskData.setCreatedby(CommonUtility.getUserId(pContext));
        updateTaskData.setTaskID(task.getTaskID());
        updateTaskData.setHelpedBy(pArrayList);
        updateTaskData.setTimeCreated(task.getTimeCreated()); //this is time when we create task
        updateTaskData.setCompleted(pCompleteOrNot);
        updateTaskData.setTaskDescription(task.getTaskDescription());
        updateTaskData.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        updateTaskData.setUserMovedOutside(pUserMoveOutside);
        updateTaskData.setRecentActivity(pRecentActivity);
        updateTaskData.setStartColor(task.getStartColor());
        updateTaskData.setEndColor(task.getEndColor());
        updateTaskData.setCompleteType(pCompleteType);
        return updateTaskData;
    }

    public void setUpdateTimeOfCurrentTask(String pKey) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pKey).addValueEventListener(new ValueEventListener() {
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
        mDatabaseReference.child("users").child(pKey).child("updatedAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserMarker userMarker = null, userMarkerGet = null;
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
                    userMarkerGet = new UserMarker();
                    userMarkerGet.setStartTime(currentTime);
                    userMarkerGet.setEndTime(taskUpdateTime);
                    userMarkerGet.setLatLng(new LatLng(pUserLocations.getLatitude(), pUserLocations.getLongitude()));
                    userMarkerGet.setKey(pKey);
                }
                ((MapActivity) mContext).setUsersIntoList(pKey, userMarker);
                ((MapActivity) mContext).setAllUsersIntoList(userMarkerGet);
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

    public void clearTaskArray() {
        if (mHashMap != null) {
            mHashMap.clear();
        }
        if (mTaskParticpatedHashMap != null) {
            mTaskParticpatedHashMap.clear();
        }
    }

    public void getTaskParticipatedByUsers(String pUserId) {
        mDatabaseReference.child("users").child(pUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                    if (hashMapUserData.containsKey("taskParticipated")) {
                        HashMap hashMapTaskParticipated = (HashMap) hashMapUserData.get("taskParticipated");
                        ArrayList arraylistTasksFetch = (ArrayList) hashMapTaskParticipated.get("tasks");
                        if (arraylistTasksFetch != null) {
                            for (int i = 0; i < arraylistTasksFetch.size(); i++)
                                mTaskParticpatedHashMap.put(String.valueOf(i), arraylistTasksFetch.get(i));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getTaskFromFirebase(final TaskLocations pTaskLocations) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pTaskLocations.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        Task task = dataSnapshot.getValue(Task.class);
                        if (task != null) {
                            if (mHashMap.containsKey(task.getTaskID())) {
                                ((MapActivity) mContext).updateTaskCardFromViewPager(task, pTaskLocations);
                            } else {
                                mHashMap.put(task.getTaskID(), task);
                                if (!task.isUserMovedOutside()) {
                                    ((MapActivity) mContext).setListsOfFetchingTask(task, pTaskLocations);
                                } else {
                                    if (task.isUserMovedOutside()) {
                                        if (mTaskParticpatedHashMap.size() > 0 && mTaskParticpatedHashMap.containsValue(task.getTaskID())) {
                                            ((MapActivity) mContext).setListsOfFetchingTask(task, pTaskLocations);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setListenerForEnteringTask(String pkey) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        Task taskData = CommonUtility.getTaskData(mContext);
                        if (task.isRecentActivity()) {
                            taskData.setRecentActivity(task.isRecentActivity());
                            CommonUtility.setTaskData(taskData, mContext);
                            ((MapActivity) mContext).setRecentActivity(task.isRecentActivity());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
        This will get last message from each user and display only last 5 messages.
     */
    public ArrayList<Message> getLastFiveMessagesFromMessagesList() {
        ArrayList<Message> messageArrayList = new ArrayList<>();
        if (mMessageHashMap.size() > 0 && mGetAllUsers.size() > 0) {
            for (Object o : mMessageHashMap.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                if (mGetAllUsers.containsKey(pair.getKey()) && !pair.getKey().equals(CommonUtility.getUserId(mContext))) {
                    messageArrayList.add((Message) pair.getValue());
                }
            }
        }
        clearMessageHashMap();
        return new ArrayList<>(messageArrayList.subList(Math.max(messageArrayList.size() - 5, 0), messageArrayList.size()));
    }

    private void clearMessageHashMap() {
        mMessageHashMap.clear();
    }

    private void getInstancePushNotificationManager() {
        if (mPushNotificationManager == null) {
            mPushNotificationManager = new PushNotificationManager();
        }
    }

    // TODO: Send Push notification to nearby users.
    public void sendPushNotificationToNearbyUsers(HashSet<UserMarker> pNearByUsers, final String pTaskId) {
        getInstancePushNotificationManager();
        for (final UserMarker userMarker : pNearByUsers) {
            mDatabaseReference.child("users").child(userMarker.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                            if (hashMapUserData.containsKey("deviceToken")) {
                                String deviceToken = hashMapUserData.get("deviceToken").toString();
                                if (deviceToken != null && !deviceToken.equals(Constants.sConstantEmptyString) && !userMarker.getKey().equals(CommonUtility.getUserId(mContext))) {
                                    mPushNotificationManager.sendNearbyTaskNotification(deviceToken, pTaskId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // TODO: send notification to the topic for specific channel id
    public void sendPushNotificationToTopic(final String pChannelId, String pMessage) {
        getInstancePushNotificationManager();
        final String currentUserId = CommonUtility.getUserId(mContext);

        channelTopicMessage = "";
        if (pMessage != null) {
            channelTopicMessage = pMessage;
        }

        mDatabaseReference.child(sChannel).child(pChannelId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        HashMap usersHashMap = (HashMap) dataSnapshot.getValue();
                        if (usersHashMap.size() > 0) {
                            for (Object o : usersHashMap.entrySet()) {
                                Map.Entry pair = (Map.Entry) o;
                                if (!pair.getKey().toString().equals(CommonUtility.getUserId(mContext))) {
                                    mDatabaseReference.child("users").child(pair.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                                                if (hashMapUserData.containsKey("deviceToken")) {
                                                    String deviceToken = hashMapUserData.get("deviceToken").toString();
                                                    if (deviceToken != null && !deviceToken.equals(Constants.sConstantEmptyString)) {
                                                        mPushNotificationManager.sendNotificationToDeviceForMessage(deviceToken, currentUserId, pChannelId, channelTopicMessage);
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // action for when users complete task
    // and some users helped
    public void handleUsersHelpedButtonPressed(String pSenderId, final Task pTask) {
        getInstancePushNotificationManager();
        mDatabaseReference.child("users").child(pSenderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        HashMap hashMapUserData = (HashMap) dataSnapshot.getValue();
                        if (hashMapUserData.containsKey("deviceToken")) {
                            String deviceToken = hashMapUserData.get("deviceToken").toString();
                            if (deviceToken != null && !deviceToken.equals(Constants.sConstantEmptyString)) {
                                mPushNotificationManager.sendYouWereThankedNotification(deviceToken, pTask.getTaskDescription());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Send Push notification If task is Completed
    //filter Admin and thank users
    public void removeTaskAfterComplete(final Task pTask, final ArrayList<Message> pThanksUser) {
        final String taskMessage = pTask.getTaskDescription();
        if (taskMessage != null && mContext != null) {
            if (pTask.getCompleteType().equals(mContext.getResources().getString(R.string.STATUS_FOR_THANKED)) || pTask.getCompleteType().equals(mContext.getResources().getString(R.string.STATUS_FOR_THANKED))) {
                mDatabaseReference.child(sChannel).child(pTask.getTaskID()).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                HashMap hashMapUsers = (HashMap) dataSnapshot.getValue();
                                if (hashMapUsers.size() > 0) {
                                    for (Object o : hashMapUsers.entrySet()) {
                                        Map.Entry pair = (Map.Entry) o;
                                        if (!pair.getKey().toString().equals(CommonUtility.getUserId(mContext))) {
                                            if (pThanksUser.size() > 0) {
                                                for (Message message : pThanksUser) {
                                                    if (message.getSenderId().equals(pair.getKey().toString())) {
                                                        mDatabaseReference.child("users").child(pair.getKey().toString()).child("deviceToken").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.getValue() != null) {
                                                                    String deviceToken = dataSnapshot.getValue().toString();
                                                                    if (deviceToken != null && !deviceToken.equals(Constants.sConstantEmptyString)) {
                                                                        mPushNotificationManager.sendNotificationToDevice(deviceToken, taskMessage, pTask.getTaskID());
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    //Update points at Firebase Server
    /*
    pUser: all the users that helps me
     */
    public void updatePointsAtFirebaseServer(final String pUser, final boolean pIsFakeCard) {
        mDatabaseReference.child("users").child(pUser).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        int scoreValue = dataSnapshot.getValue(Integer.class);
                        scoreValue += 1;
                        mDatabaseReference.child("users").child(pUser).child("score").setValue(scoreValue);
                        updateScoreDetailAtFirebaseServer(pUser, 1, pIsFakeCard);
                        if (!pIsFakeCard) {
                            updateFriendsAtFirebaseServer(pUser);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateFriendsAtFirebaseServer(final String pHelpUsers) {
        final String user = CommonUtility.getUserId(mContext);
        if (user != null && !user.equals(Constants.sConstantEmptyString)) {
            mDatabaseReference.child("users").child(user).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        int i = 0;
                        if (dataSnapshot.getValue() != null) {
                            ArrayList arrayList = (ArrayList) dataSnapshot.getValue();
                            HashMap<String, String> hashMapFriendsList = new HashMap<>();
                            for (i = 0; i < arrayList.size(); i++) {
                                hashMapFriendsList.put(String.valueOf(i), arrayList.get(i).toString());
                            }
                            if (!hashMapFriendsList.containsValue(pHelpUsers)) {
                                hashMapFriendsList.put(String.valueOf(i), pHelpUsers);
                                mDatabaseReference.child("users").child(user).child("friends").setValue(hashMapFriendsList);
                            }
                        } else {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(String.valueOf(0), pHelpUsers);
                            mDatabaseReference.child("users").child(user).child("friends").setValue(hashMap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void updateScoreDetailAtFirebaseServer(final String pUser, final int pPoints, final boolean pIsFakeCard) {
        mDatabaseReference.child("users").child(pUser).child("scoreDetail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        ArrayList arrayList = (ArrayList) dataSnapshot.getValue();
                        ArrayList<ScoreDetail> arrayListNewScoreDetail = new ArrayList<>();
                        for (int i = 0; i < arrayList.size(); i++) {
                            HashMap hashMap = (HashMap) arrayList.get(i);
                            ScoreDetail scoreDetail = new ScoreDetail();
                            if (hashMap.containsKey("createdDate")) {
                                scoreDetail.setCreatedDate(hashMap.get("createdDate").toString());
                            }
                            if (hashMap.containsKey("taskID")) {
                                scoreDetail.setTaskID(hashMap.get("taskID").toString());
                            }
                            if (hashMap.containsKey("points")) {
                                scoreDetail.setPoints(Integer.parseInt(hashMap.get("points").toString()));
                            }
                            arrayListNewScoreDetail.add(scoreDetail);
                        }
                        ScoreDetail scoreDetail = new ScoreDetail();
                        if (pIsFakeCard) {
                            scoreDetail.setTaskID(CommonUtility.convertLocalTimeToUTC());
                        } else {
                            Task task = CommonUtility.getTaskData(mContext);
                            scoreDetail.setTaskID(task.getTaskID());
                        }
                        scoreDetail.setPoints(pPoints);
                        scoreDetail.setCreatedDate(CommonUtility.getLocalTime());
                        arrayListNewScoreDetail.add(scoreDetail);
                        mDatabaseReference.child("users").child(pUser).child("scoreDetail").setValue(arrayListNewScoreDetail);
                    } else {
                        ScoreDetail scoreDetail = new ScoreDetail();
                        if (pIsFakeCard) {
                            scoreDetail.setTaskID(CommonUtility.convertLocalTimeToUTC());
                        } else {
                            Task task = CommonUtility.getTaskData(mContext);
                            scoreDetail.setTaskID(task.getTaskID());
                        }
                        scoreDetail.setPoints(pPoints);
                        scoreDetail.setCreatedDate(CommonUtility.getLocalTime());
                        ArrayList<ScoreDetail> arrayList = new ArrayList<>();
                        arrayList.add(scoreDetail);
                        mDatabaseReference.child("users").child(pUser).child("scoreDetail").setValue(arrayList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //add observer to fetch score
    public void getPointsFromFirebase(String pUser) {
        mPointsListener = mDatabaseReference.child("users").child(pUser).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        int scoreValue = dataSnapshot.getValue(Integer.class);
                        if (mContext != null) {
                            ((MapActivity) mContext).setScoreIntoView(scoreValue, isThanksDialogAnimationRun);
                            isThanksDialogAnimationRun = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removePointListener() {
        if (mPointsListener != null) {
            mDatabaseReference.removeEventListener(mPointsListener);
        }
    }

    public void processMessageNotification(String pChannelId) {
        mDatabaseReference.child(swriteNew_UpdateTask).child(pChannelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        //Check Task Status
                        boolean taskComplete = false;
                        boolean needToPush = false;
                        Task task = dataSnapshot.getValue(Task.class);
                        if (task != null) {
                            if (task.isCompleted()) {
                                taskComplete = true;
                            }
                            String taskDescription = task.getTaskDescription();
                            if (taskDescription != null) {
                                if (mContext instanceof MapActivity) {
                                    ((MapActivity) mContext).openChatViewFromNotification(taskDescription, taskComplete, task.getTaskID());
                                    needToPush = true;
                                }
                                if (mContext instanceof ChatActivity) {
                                    ((ChatActivity) mContext).setData(taskDescription, taskComplete, task.getTaskID());
                                    needToPush = true;
                                }
                            }
                            if (taskComplete) {
                                if (mContext instanceof MapActivity) {
                                    ((MapActivity) mContext).taskExpireAlert();
                                }
                                if (mContext instanceof ChatActivity) {
                                    ((ChatActivity) mContext).taskExpireAlert();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "exception occur:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}