package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.github.sahasbhop.apngview.ApngDrawable;
import com.github.sahasbhop.apngview.ApngImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.adapters.ThanksChatAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.backgroundservice.BackgroundLocationService;
import com.mayo.classes.AnimateCard;
import com.mayo.classes.CardsDataModel;
import com.mayo.classes.GeoFencing;
import com.mayo.classes.MarkerClick;
import com.mayo.classes.ShownCardMarker;
import com.mayo.classes.TaskTimer;
import com.mayo.classes.UserLiveMarker;
import com.mayo.classes.ViewPagerScroller;
import com.mayo.firebase.database.FirebaseDatabase;
import com.mayo.interfaces.LocationUpdationInterface;
import com.mayo.interfaces.OnItemClickListener;
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.CardLatlng;
import com.mayo.models.MarkerTag;
import com.mayo.models.Message;
import com.mayo.models.TaskLatLng;
import com.mayo.models.TaskLocations;
import com.mayo.models.UserMarker;
import com.mayo.models.MapDataModel;
import com.mayo.models.Task;
import com.mayo.classes.CustomViewPager;
import com.mayo.classes.FakeMarker;
import com.mayo.classes.GeoFireClass;
import com.mayo.models.UsersLocations;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static com.mayo.Utility.CommonUtility.isLocationEnabled;

@SuppressLint("Registered")
@EActivity(R.layout.activity_map)
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationUpdationInterface, ViewClickListener, GoogleMap.OnMapClickListener, ResultCallback<Status>, OnItemClickListener {

    @App
    MayoApplication mMayoApplication;

    @ViewById(R.id.mapView)
    MapView mMapView;

    @ViewById(R.id.countbutton)
    Button mCountButton;

    @ViewById(R.id.viewPagerMapView)
    CustomViewPager mViewPagerMap;

    @ViewById(R.id.relativeMapLayout)
    RelativeLayout mRelativeMapLayout;

    @ViewById(R.id.imageHandsViewOnMapScreen)
    ImageView mImageHandsViewOnMap;

    @ViewById(R.id.rotateImageOnMapView)
    ImageView mRotateImageOnMapView;

    @ViewById(R.id.parentQuestLayout)
    LinearLayout mParentQuestLayout;

    @ViewById(R.id.questButton)
    Button questButton;

    @ViewById(R.id.cancelButton)
    Button cancelButton;

    @ViewById(R.id.fadeInoutCard)
    CardView mFadeInOutCardView;

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Double mCurrentLat, mCurrentLng;
    private GroundOverlay mCurrentLocationGroundOverlay;
    private Marker mCurrentLocationMarker;
    private ArrayList<UserMarker> mFakeUserMarker;
    private boolean isMarkerClick = false, isScrollingRight = false, isNewTaskEnter = false;
    private BackgroundLocationService mBackgroundLocationService;
    ArrayList<MapDataModel> mMapDataModels;
    Dialog mDialog;
    int positionNew;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private GeoFireClass mGeoFireClass;
    private GeoQuery mGeoQuery, mTaskGeoQuery;
    private Location mCurrentLocation, mTaskLocation, mCurrentLocationForCardMarker;
    private FakeMarker mFakeMarker;
    private UserLiveMarker mUserLiveMarker;
    private ApngDrawable mApngDrawable;
    private HashSet<UserMarker> mNearByUsers;
    private ArrayList<TaskLocations> mTaskLocationsArray;
    private ArrayList<TaskLatLng> mTasksArray;
    private ShownCardMarker mShownCardMarker;
    private ViewPagerScroller mViewPagerScroller;
    private MarkerClick mMarkerClick;
    private float sumPositionAndPositionOffset = 0;
    private RecyclerView mHelpRecyclerView;
    private CardsDataModel mCardsDataModel;
    private TaskTimer mTaskTimer;
    private FirebaseDatabase mFirebaseDatabase;
    private Date lastUpdateTime;
    private GeoFencing mGeoFencing;
    private GoogleReceiver mReceiver;
    private boolean isThanksDialogOpen = false;
    private ArrayList<Message> mMessageList;
    private ArrayList<Message> mhelpMessageList;
    private TextView mThanksTextView;

    @AfterViews
    protected void init() {
        mMayoApplication.setActivity(this);
        mMapView.onCreate(null);
        mNearByUsers = new HashSet<>();
        mTaskLocationsArray = new ArrayList<>();
        mTasksArray = new ArrayList<>();
        mhelpMessageList = new ArrayList<>();
        registerReceiver();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ApngImageLoader.getInstance().displayImage("assets://apng/fist_bump_720p.png", mImageHandsViewOnMap);
        mFakeUserMarker = new ArrayList<>();
        mRelativeMapLayout.setFitsSystemWindows(true);
        setDataModel();
        setTouchListenerOfViewPager();
        checkGoogleServiceAvailable();
        setGeoFire();
        mCountButton.setText(String.valueOf(CommonUtility.getPoints(this)));
        mCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPointView();
            }
        });
        if (CommonUtility.getFakeCardOne(this) && CommonUtility.getFakeCardTwo(this) && CommonUtility.getFakeCardThree(this)) {
            CommonUtility.setFakeCardShownOrNot(false, this);
            AnimateCard animateCard = new AnimateCard(this, mFadeInOutCardView, mViewPagerMap);
            animateCard.playFadeInOutAnimation();
        }
        if (mGeoFencing == null) {
            mGeoFencing = new GeoFencing(this);
        }
        getFirebaseInstance();
        mFirebaseDatabase.getTaskParticipatedByUsers(CommonUtility.getUserId(this));
        mFirebaseDatabase.getUserIdsFromFirebase();
        if (CommonUtility.getTaskApplied(this)) {
            Task task = CommonUtility.getTaskData(this);
            mFirebaseDatabase.getAllMessagesOfTaskEnteredByUser(task.getTaskID(), this);
        }
    }

    public void setViewPagerData() {
        setViewPager();
        if (mViewPagerScroller == null) {
            scrollViewPager();
        }
        if (CommonUtility.getTaskApplied(this) && mTaskTimer == null) {
            getFirebaseInstance();
            mFirebaseDatabase.setUpdateTimeOfCurrentTask(CommonUtility.getTaskData(this).getTaskID());
            mFirebaseDatabase.setListenerForEnteringTask(CommonUtility.getTaskData(this).getTaskID());
        }
    }

    private void setGeoFire() {
        mGeoFireClass = new GeoFireClass(this);
        mGeoFireClass.setGeoFire();
    }

    private void checkGoogleServiceAvailable() {
        if (CommonUtility.googleServicesAvailable(this)) {
            mMapView.getMapAsync(MapActivity.this);
        } else {
            mMayoApplication.showToast(this, getResources().getString(R.string.notsupported));
        }
    }

    private boolean checkPermissions() {
        boolean isPermission = true;
        String permission[] = CommonUtility.checkPermissions();
        for (String aPermission : permission) {
            if (ContextCompat.checkSelfPermission(this, aPermission) != PackageManager.PERMISSION_GRANTED) {
                locationNotEnabled();
                disableLocationDialogView(Constants.PermissionDialog.AppPermissionDialog.ordinal());
                isPermission = false;
                break;
            }
        }
        return isPermission;
    }


    private void showPointView() {
        final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.score_screen);
        if (dialog != null) {
            ImageButton deleteView = (ImageButton) dialog.findViewById(R.id.delete_view);
            TextView score = (TextView) dialog.findViewById(R.id.points);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            score.setText(String.valueOf(CommonUtility.getPoints(MapActivity.this)));
        }
        getCurrentLocation();
    }

    public void getCurrentLocation() {
        if (mGoogleMap != null && mCurrentLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .zoom(Constants.sKeyCameraZoom).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setTouchListenerOfViewPager() {
        mViewPagerMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isMarkerClick = false;
                return false;
            }
        });
    }

    private void scrollViewPager() {
        mViewPagerScroller = new ViewPagerScroller(this, mViewPagerMap, mMapDataModels,
                mMapViewPagerAdapter, mShownCardMarker, mCurrentLocationForCardMarker, mGoogleMap);

        mViewPagerMap.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isScrollingRight = !(position + positionOffset > sumPositionAndPositionOffset);
                sumPositionAndPositionOffset = position + positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                int Type = mMapDataModels.get(position).getFakeCardPosition();
                int value;
                Constants.CardType cardType = Constants.CardType.values()[Type];
                if (position != Constants.CardType.POST.getValue()) {
                    if (CommonUtility.getSoftKeyBoardState(MapActivity.this)) {
                        mMayoApplication.hideKeyboard(getCurrentFocus());
                        CommonUtility.setSoftKeyBoardState(false, MapActivity.this);
                    }
                }
                if (!isMarkerClick) {
                    switch (cardType) {
                        case POST:
                            value = mViewPagerScroller.getPostCard();
                            if (value != -1) {
                                mCountButton.setText(String.valueOf(value));
                            }
                            mMapViewPagerAdapter.setTaskCardViewVisible();
                            break;
                        case FAKECARDONE:
                            mViewPagerScroller.getFakeCardOne();
                            break;
                        case FAKECARDTWO:
                            value = mViewPagerScroller.getFakeCardTwo(isScrollingRight);
                            if (value != -1) {
                                mCountButton.setText(String.valueOf(value));
                            }
                            break;
                        case FAKECARDTHREE:
                            value = mViewPagerScroller.getFakeCardThree(isScrollingRight);
                            if (value != -1) {
                                mCountButton.setText(String.valueOf(value));
                            }
                            break;
                        case DEFAULT:
                            mViewPagerScroller.setLiveCardViewMarker(mViewPagerMap.getCurrentItem());
                            getFirebaseInstance();
                            if (mMapDataModels.get(mViewPagerMap.getCurrentItem()).getTaskLatLng() != null) {
                                String timeStamp = mMapDataModels.get(mViewPagerMap.getCurrentItem()).getTaskLatLng().getTask().getTaskID();
                                mFirebaseDatabase.setTaskViewsByUsers(timeStamp);
                            }
                            break;
                    }
                    positionNew = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setDataModel() {
        if (mCardsDataModel == null) {
            mCardsDataModel = new CardsDataModel(this, mTasksArray);
        }
        if (mMapDataModels == null) {
            mMapDataModels = mCardsDataModel.getDataModel();
        }
    }

    private void setViewPager() {
        mViewPagerMap.setPagingEnabled(true);
        mViewPagerMap.setClipToPadding(false);
        mViewPagerMap.setPadding(Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding,
                Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding);
        mViewPagerMap.setPageMargin(Constants.CardMarginSetValues.sMarginValue);
        mMapViewPagerAdapter = new MapViewPagerAdapter(this, mMapDataModels, this, mViewPagerMap, this, mMayoApplication);
        if (mCardsDataModel != null) {
            mCardsDataModel.setViewPagerAdapter(mMapViewPagerAdapter);
        }
        if (mShownCardMarker != null) {
            mShownCardMarker.getAnotherUsersLiveMarker();
        }
        mViewPagerMap.setAdapter(mMapViewPagerAdapter);
        if (!CommonUtility.getFakeCardShownOrNot(this)) {
            mViewPagerMap.setCurrentItem(0);
            new com.mayo.Utility.CountDown(this, 500, 100, mViewPagerMap, mMapDataModels);
        } else if (mMapDataModels.size() > 1) {
            mViewPagerMap.setCurrentItem(1);
        }
        positionNew = mViewPagerMap.getCurrentItem();
    }

    private void setCardMarkers(Location pLocation) {
        mShownCardMarker = new ShownCardMarker(this, mMapDataModels, pLocation, mGoogleMap);
        mShownCardMarker.getCardMarkers();
        setOnMarkerClickListener();
        setTaskMarker();
    }

    private void setTaskMarker() {
        if (CommonUtility.getTaskApplied(this)) {
            mShownCardMarker.setTaskMarker(CommonUtility.getTaskLocation(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (CommonUtility.getHandsAnimationShownOnMap(MapActivity.this)) {
            mImageHandsViewOnMap.setVisibility(View.VISIBLE);
            mApngDrawable = ApngDrawable.getFromView(mImageHandsViewOnMap);
            mRotateImageOnMapView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(MapActivity.this, R.anim.rotate);
            mRotateImageOnMapView.startAnimation(animation);
            CommonUtility.setHandsAnimationShownOnMap(false, MapActivity.this);
            mRelativeMapLayout.setFitsSystemWindows(false);
            new CountDown(4000, 1000);
            if (mApngDrawable == null)
                return;
            mApngDrawable.setNumPlays(0);
            mApngDrawable.start();
        }
        if (mGoogleMap != null) {
            if (mDialog != null && checkPermissions()) {
                mDialog.dismiss();
                mDialog = null;
            }
            if (!isLocationEnabled(this)) {
                locationNotEnabled();
                disableLocationDialogView(Constants.PermissionDialog.LocationDialog.ordinal());
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMayoApplication.hideKeyboard(getCurrentFocus());
        CommonUtility.setSoftKeyBoardState(false, MapActivity.this);
        if (mMapViewPagerAdapter != null)
            mMapViewPagerAdapter.setPostCardText();
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (!status.isSuccess()) {
            String errorMessage = mGeoFencing.getErrorString(this, status.getStatusCode());
            Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(View view, int position, boolean isSelected) {
        Message message = mMessageList.get(position);
        if (isSelected) {
            mhelpMessageList.add(message);
        } else {
            for (int i = 0; i < mhelpMessageList.size(); i++) {
                if (mhelpMessageList.get(i).getSenderId().equals(message.getSenderId())) {
                    mhelpMessageList.remove(i);
                }
            }
        }
        if (mThanksTextView != null) {
            if (mhelpMessageList.size() > 0) {
                mThanksTextView.setAlpha(Constants.sNonTransparencyLevel);
            } else {
                mThanksTextView.setAlpha(Constants.sTransparencyLevelFade);
            }
        }
    }

    private class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onFinish() {
            mRotateImageOnMapView.setVisibility(View.GONE);
            mRotateImageOnMapView.clearAnimation();
            if (mMapViewPagerAdapter != null) {
                for (int i = 0; i < mMapDataModels.size(); i++) {
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                        mMapViewPagerAdapter.deleteItemFromArrayList(i);
                        mMapDataModels.get(i).getCardLatlng().getMarker().remove();
                        int value = CommonUtility.getPoints(MapActivity.this) + 1;
                        CommonUtility.setPoints(value, MapActivity.this);
                        mCountButton.setText(String.valueOf(value));
                        CommonUtility.setFakeCardTwo(true, MapActivity.this);
                    }
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                        CardLatlng cardLatlng = mMapDataModels.get(i).getCardLatlng();
                        cardLatlng.getMarker().setIcon(mShownCardMarker.getIconLarge());
                        cardLatlng.getMarker().setZIndex(Constants.sMarkerZIndexMaximum);
                        mShownCardMarker.setGoogleMapPosition(mCurrentLocationForCardMarker, cardLatlng);
                        break;
                    }
                }
            }
            mRelativeMapLayout.setFitsSystemWindows(true);
            if (mApngDrawable == null)
                return;
            if (mApngDrawable.isRunning()) {
                mApngDrawable.stop(); // Stop animation
            }
            mImageHandsViewOnMap.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long duration) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(this);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(true);
        if (!isLocationEnabled(this)) {
            locationNotEnabled();
            disableLocationDialogView(Constants.PermissionDialog.LocationDialog.ordinal());
            return;
        }
        checkPermissions();
        mGoogleMap.setOnMapClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this).
                addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mLocationRequest = LocationRequest.create();
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        getLocation();
                        startService();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).build();
        mGoogleApiClient.connect();
    }


    private void locationNotEnabled() {
        if (mGoogleMap != null) {
            LatLng latLng = CommonUtility.getUserLocation(this);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.sKeyCameraZoom));
        }
    }

    private void startService() {
        // Starting a service for update location in background
        Intent serviceIntent = new Intent(MapActivity.this, BackgroundLocationService.class);
        startService(serviceIntent);
        Intent intent = new Intent(this, BackgroundLocationService.class);
        getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            BackgroundLocationService.LocalBinder binder = (BackgroundLocationService.LocalBinder) service;
            mBackgroundLocationService = binder.getServerInstance();
            mBackgroundLocationService.setCallbacks(MapActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location == null && getApplicationContext() != null) {
                    mMayoApplication.showToast(getApplicationContext(), getResources().getString(R.string.cantgetlocation));
                } else {
                    if (location != null) {
                        if (mCurrentLocationForCardMarker == null) {
                            mCurrentLocationForCardMarker = location;
                        }
                        setCurrentLocation(location);
                        sendDataToFirebaseOfUserLocation(location);
                        if (mShownCardMarker == null) {
                            setCardMarkers(location);
                        }
                        if (mGeoQuery == null) {
                            setGeoQuery();
                        }
                    }
                }
            }
        });
    }

    /**
     * set current location of user and add marker and circle on current location
     *
     * @param location current location of user
     */
    private void setCurrentLocation(Location location) {
        mCurrentLocation = location;
        setUpGeoFencingForTask(location.getLatitude(), location.getLongitude());
        CommonUtility.setUserLocation(mCurrentLocation, this);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLat = location.getLatitude();
        mCurrentLng = location.getLongitude();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, Constants.sKeyCameraZoom);
        CameraPosition cameraPosition = new CameraPosition(ll, 45, 45, 45);
        update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.animateCamera(update);
        if (mCurrentLocationGroundOverlay == null) {
            drawCircle(location);
        }
        if (mCurrentLocationMarker == null) {
            addCurrentLocationMarker(location);
        }
    }

    private void drawCircle(Location location) {
        mCurrentLocation = location;
        setGroundOverlayForShowingCircle(mCurrentLocation);
    }

    private void setOnMarkerClickListener() {
        mMarkerClick = new MarkerClick(this, mViewPagerMap, mMapDataModels);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null && mViewPagerScroller != null) {
                    isMarkerClick = true;
                    if (marker.getTag().toString().equals(String.valueOf(Constants.CardType.FAKECARDTHREE.getValue()))
                            || marker.getTag().toString().equals(String.valueOf(Constants.CardType.FAKECARDONE.getValue()))
                            || marker.getTag().toString().equals(String.valueOf(Constants.CardType.FAKECARDTWO.getValue()))
                            || marker.getTag().toString().equals(String.valueOf(Constants.CardType.POST.getValue()))) {
                        int markerGet = Integer.parseInt(marker.getTag().toString());
                        Constants.CardType cardType = Constants.CardType.values()[markerGet];
                        switch (cardType) {
                            case POST:
                                mMarkerClick.getPostCardMarker();
                                mViewPagerScroller.setFakeCardLargeIcon(Constants.CardType.POST.getValue());
                                mViewPagerScroller.clearExpireCardMarker();
                                break;
                            case FAKECARDONE:
                                mMarkerClick.getFirstFakeCard();
                                mViewPagerScroller.setFakeCardLargeIcon(Constants.CardType.FAKECARDONE.getValue());
                                mViewPagerScroller.clearExpireCardMarker();
                                break;
                            case FAKECARDTWO:
                                mMarkerClick.getSecondFakeCard();
                                mViewPagerScroller.setFakeCardLargeIcon(Constants.CardType.FAKECARDTWO.getValue());
                                mViewPagerScroller.clearExpireCardMarker();
                                break;
                            case FAKECARDTHREE:
                                mMarkerClick.getThirdFakeCard();
                                mViewPagerScroller.setFakeCardLargeIcon(Constants.CardType.FAKECARDTHREE.getValue());
                                mViewPagerScroller.clearExpireCardMarker();
                                break;
                        }
                    } else {
                        if (marker.getTag() != null && marker.getTag() instanceof MarkerTag) {
                            int count = 0;
                            if (CommonUtility.getFakeCardOne(MapActivity.this)) {
                                count += 1;
                            }
                            if (CommonUtility.getFakeCardTwo(MapActivity.this)) {
                                count += 1;
                            }
                            if (CommonUtility.getFakeCardThree(MapActivity.this)) {
                                count += 1;
                            }
                            MarkerTag markerTag = ((MarkerTag) marker.getTag());
                            int position = Integer.parseInt(markerTag.getIdNew()) - count;
                            mMarkerClick.getCardMarker(position);
                            mViewPagerScroller.setLiveUserMarkerLarge(position);
                            mViewPagerScroller.clearExpireCardMarker();
                        }
                    }
                }
                return true;
            }
        });
    }

    private void setGroundOverlayForShowingCircle(Location location) {
        Bitmap bitmap = CommonUtility.drawableToBitmapForCircle(ContextCompat.getDrawable(MapActivity.this, R.drawable.location_circle_radius));
        BitmapDescriptor currentLocationImage = BitmapDescriptorFactory.fromBitmap(bitmap);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.position(new LatLng(location.getLongitude(), location.getLatitude()), 200f * 2);
        groundOverlayOptions.image(currentLocationImage).transparency(0.2f);
        mCurrentLocationGroundOverlay = mGoogleMap.addGroundOverlay(groundOverlayOptions);
        mCurrentLocationGroundOverlay.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void addCurrentLocationMarker(Location location) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(MapActivity.this, R.drawable.location_circle));
        BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        MarkerOptions currentLocationMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(getResources().getString(R.string.my_location))
                .icon(currentLocationIcon);
        mCurrentLocationMarker = mGoogleMap.addMarker(currentLocationMarker);
    }

    private void setFakeMarker(Location location) {
        if (mFakeUserMarker != null && mFakeUserMarker.size() > 0) {
            removeFakeMarkers(mFakeUserMarker);
        }
        int numOfFakeUsers = FakeMarker.generateRandomMarkerOfFakeUsers();
        for (int i = 0; i < numOfFakeUsers; i++) {
            addFakeUserMaker(location);
        }
        if (mFakeUserMarker.size() > 0) {
            CommonUtility.setFakeMarkerShown(true, MapActivity.this);
            mFakeMarker = new FakeMarker(MapActivity.this, mFakeUserMarker);
            mFakeMarker.startTimer();
        }
    }

    private void addFakeUserMaker(Location location) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(MapActivity.this, R.drawable.location_fake_users_circle));
        BitmapDescriptor fakeLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        double lat, lng;
        int latlngContainNumber = FakeMarker.generateRandomLocationOfFakeUser();
        lat = FakeMarker.fakeUserChoices[latlngContainNumber][0];
        lng = FakeMarker.fakeUserChoices[latlngContainNumber][1];
        LatLng latLng = new LatLng(location.getLatitude() + lat, location.getLongitude() + lng);
        MarkerOptions fakeMarker = new MarkerOptions().position(latLng)
                .icon(fakeLocationIcon);
        setFakeUserModel(fakeMarker, latLng);
    }

    public void removeFakeMarkerAccodingToTime(int pIndex) {
        if (mFakeUserMarker.size() > 0) {
            mFakeUserMarker.get(pIndex).getMarker().remove();
            mFakeUserMarker.remove(pIndex);
        }
        if (mFakeUserMarker.size() == 0) {
            CommonUtility.setFakeMarkerShown(true, MapActivity.this);
            if (mFakeMarker != null) {
                mFakeMarker.stoptimertask();
            }
        }
    }

    public void removeUserMarkerAccordingToTime(UserMarker pUserMarker) {
        pUserMarker.getMarker().remove();
        mNearByUsers.remove(pUserMarker);
        if (mNearByUsers.size() == 0) {
            if (mUserLiveMarker != null) {
                mUserLiveMarker.stoptimertask();
                mUserLiveMarker = null;
            }
        }
    }

    private void setFakeUserModel(MarkerOptions pFakeMarker, LatLng pLatlng) {
        UserMarker fakeUsersShown = new UserMarker();
        fakeUsersShown.setMarker(mGoogleMap.addMarker(pFakeMarker));
        fakeUsersShown.setLatLng(pLatlng);
        fakeUsersShown.setStartTime(CommonUtility.getCurrentTime());
        fakeUsersShown.setEndTime(CommonUtility.getEndTimeOfFakeUsers(FakeMarker.generateEndTimeOfMarkerShown()));
        mFakeUserMarker.add(fakeUsersShown);
    }

    //remove all fake markers
    private void removeFakeMarkers(ArrayList<UserMarker> pFakeMarker) {
        ArrayList<UserMarker> fakeMarkerLocal = new ArrayList<>();
        fakeMarkerLocal.addAll(pFakeMarker);
        for (int i = 0; i < fakeMarkerLocal.size(); i++) {
            fakeMarkerLocal.get(i).getMarker().remove();
        }
        fakeMarkerLocal.removeAll(pFakeMarker);
        mFakeUserMarker = fakeMarkerLocal;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mFakeMarker != null) {
            mFakeMarker.stoptimertask();
            CommonUtility.setFakeMarkerShown(false, MapActivity.this);
        }
        if (mUserLiveMarker != null) {
            mUserLiveMarker.stoptimertask();
        }
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void updateLocation(Location location) {
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        mCurrentLocation = location;
        CommonUtility.setUserLocation(mCurrentLocation, this);
        addCurrentLocationMarker(mCurrentLocation);
        sendDataToFirebaseOfUserLocation(location);
        Date currentTime = CommonUtility.getCurrentTime();
        getFirebaseInstance();
        if (lastUpdateTime == null) {
            lastUpdateTime = currentTime;
            sendUsersDataToFirebase();
        } else {
            if (currentTime != null) {
                long timeDifference = currentTime.getTime() - lastUpdateTime.getTime();
                if (timeDifference > Constants.sLocationUpdateTime) {
                    lastUpdateTime = currentTime;
                    mFirebaseDatabase.writeNewUserLocation(CommonUtility.getUserId(this), mCurrentLocation);
                }
            }
        }
    }

    private void sendUsersDataToFirebase() {
        if (CommonUtility.getFakeCardShownOrNot(this)) {
            mFirebaseDatabase.writeNewUserData(CommonUtility.getUserId(this), CommonUtility.getLocalTime(),
                    CommonUtility.getDeviceToken(this), false, mCurrentLocation);
        } else {
            mFirebaseDatabase.writeNewUserData(CommonUtility.getUserId(this), CommonUtility.getLocalTime(),
                    CommonUtility.getDeviceToken(this), true, mCurrentLocation);
        }
    }

    /**
     * send current location data to firebase
     *
     * @param location current location of user send to firebase using geoquery
     */

    private void sendDataToFirebaseOfUserLocation(Location location) {
        mGeoFireClass.sendDataToFirebaseOfUserLocation(location);
    }

    public void setGeoFireCompleteListener() {
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        addCurrentLocationMarker(mCurrentLocation);
        getCurrentLocation();
    }

    private void sendDataToFirebaseOfTaskLocation(Location location, String pTimeStamp) {
        mTaskLocation = location;
        getFirebaseInstance();
        GeoFire geoFire = mFirebaseDatabase.setTaskLocationWithGeoFire();
        if (mTaskLocation != null) {
            geoFire.setLocation(pTimeStamp,
                    new GeoLocation(mTaskLocation.getLatitude(), mTaskLocation.getLongitude()));
            mShownCardMarker.setTaskMarker(mTaskLocation);
            startGeoFencing(mTaskLocation.getLatitude(), mTaskLocation.getLongitude());
        }
    }

    private void startGeoFencing(double lat, double lng) {
        Geofence geofence = mGeoFencing.createGeofences(lat, lng);
        if (geofence != null) {
            GeofencingRequest geofencingRequest = mGeoFencing.createGeofenceRequest(geofence);
            if (geofencingRequest != null) {
                PendingResult<Status> statusPendingResult = mGeoFencing.addGeofence(geofencingRequest, mGoogleApiClient);
                statusPendingResult.setResultCallback(this);
            }
        }
    }

    public void setUpGeoFencingForTask(double pLat, double pLong) {
        if (pLat != 0.0 && pLong != 0.0) {
            if (CommonUtility.getTaskApplied(this)) {
                LatLng latLng = CommonUtility.getTaskLocation(this);
                double distance = mMayoApplication.distance(pLat, pLong, latLng.latitude, latLng.longitude);
                if (distance > Constants.GeoFencing.sGeoFenceDitance) {
                    userMovedAway();
                } else {
                    startGeoFencing(latLng.latitude, latLng.longitude);
                }
            }

        }
    }

    /**
     * perform GeoQuery on every 200 m radius
     */
    private void setGeoQuery() {
        mNearByUsers.clear();
        mGeoQuery = mGeoFireClass.setGeoQuery(mCurrentLocation);
        mTaskGeoQuery = mGeoFireClass.setGeoQueryForTaskFetch(mCurrentLocation);
        mCardsDataModel.waitingTimeToFetchTaskArrayList(8000, 1000);
    }

    public void getNearByUsers(String pKey, GeoLocation pGeoLocation) {
        UsersLocations usersLocations = new UsersLocations();
        usersLocations.setKey(pKey);
        usersLocations.setLatitude(pGeoLocation.latitude);
        usersLocations.setLongitude(pGeoLocation.longitude);
        setRealTimeUsers(usersLocations);
    }

    public void removeCardsFromViewPager() {
        mCardsDataModel.setViewPagerAdapter(mMapViewPagerAdapter);
        mCardsDataModel.removeCardListFromView();
        mTasksArray.clear();
        mTaskLocationsArray.clear();
    }

    public void setRealTimeUsers(UsersLocations usersLocations) {
        getFirebaseInstance();
        String key = usersLocations.getKey();
        if (usersLocations.getKey().contains("Optional(")) {
            key = key.substring(key.indexOf("(") + 2, key.length() - 2);
            mFirebaseDatabase.getUsersCurrentTimeFromFirebase(key, usersLocations, mGoogleMap);
        } else {
            mFirebaseDatabase.getUsersCurrentTimeFromFirebase(key, usersLocations, mGoogleMap);
        }
    }

    public void getNearByTask(String pKey, GeoLocation pGeoLocation, boolean isNewCardEnter) {
        TaskLocations taskLocations = new TaskLocations();
        taskLocations.setKey(pKey);
        taskLocations.setLatitude(pGeoLocation.latitude);
        taskLocations.setLongitude(pGeoLocation.longitude);
        Task task = CommonUtility.getTaskData(this);
        if (task != null && pKey.equals(task.getTaskID()) && !task.isCompleted()) {
            return;
        }
        mTaskLocationsArray.add(taskLocations);
        if (isNewCardEnter) {
            isNewTaskEnter = isNewCardEnter;
            getFirebaseInstance();
            mFirebaseDatabase.getTaskFromFirebase(mTaskLocationsArray.get(mTaskLocationsArray.size() - 1));
        }
    }

    public void fetchAfterNearByTask() {
        getFirebaseInstance();
        mFirebaseDatabase.clearTaskArray();
        if (mTaskLocationsArray.size() > 0) {
            for (int i = 0; i < mTaskLocationsArray.size(); i++) {
                mFirebaseDatabase.getTaskFromFirebase(mTaskLocationsArray.get(i));
            }
        }
    }

    private void getFirebaseInstance() {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = new FirebaseDatabase(this);
        }
    }

    public void setUsersIntoList(String pKey, UserMarker pUserMarker) {
        if (!pKey.equals(CommonUtility.getUserId(this))) {
            if (pUserMarker != null) {
                mNearByUsers.add(pUserMarker);
            }
            if (mNearByUsers.size() > 0) {
                if (mUserLiveMarker == null) {
                    mUserLiveMarker = new UserLiveMarker(this, mNearByUsers);
                    mUserLiveMarker.startTimer();
                }
            }
            if (mNearByUsers.size() <= 3) {
                if (!CommonUtility.getFakeMarkerShown(this)) {
                    showFakeMarker();
                }
            }
        }
    }

    public void setListsOfFetchingTask(Task pTask, TaskLocations pTaskLocations) {
        TaskLatLng taskLatLng = new TaskLatLng();
        taskLatLng.setTask(pTask);
        taskLatLng.setTaskLocations(pTaskLocations);
        mTasksArray.add(taskLatLng);
        if (isNewTaskEnter) {
            MapDataModel mapDataModel = mCardsDataModel.getMapModelData(taskLatLng);
            mShownCardMarker.setOtherUsersTaskMarker(mapDataModel, 4);
            int count = 0;
            if (CommonUtility.getFakeCardOne(this)) {
                count += 1;
            }
            if (CommonUtility.getFakeCardTwo(this)) {
                count += 1;
            }
            if (CommonUtility.getFakeCardThree(this)) {
                count += 1;
            }
            mMapDataModels.add(4 - count, mapDataModel);
            mShownCardMarker.setMarkerTagsOnNewTaskFetch(mMapDataModels, 5 - count);
            mCardsDataModel.setViewPagerAdapter(mMapViewPagerAdapter);
            mCardsDataModel.sortNonExpiredCardViewList(mMapDataModels);
            isNewTaskEnter = false;
        }
    }

    public void updateTaskCardFromViewPager(Task pTask, TaskLocations pTaskLocations) {
        mCardsDataModel.setListOnUpdationOfTask(pTask, mMapDataModels, pTaskLocations);
        if (mMapViewPagerAdapter != null) {
            mMapViewPagerAdapter.notifyDataSetChanged();
        }
        if (mMayoApplication.gettActivityName().equals(ChatActivity_.class.getSimpleName()) &&
                mMayoApplication.isActivityVisible() && pTask.isCompleted()) {
            mMayoApplication.hideKeyboard(mMayoApplication.getActivity().getCurrentFocus());
            mMayoApplication.getActivity().finish();
            mMayoApplication.setActivity(this);
        }

    }

    private void showFakeMarker() {
        if (isLocationEnabled(this) && mCurrentLocation != null) {
            if (mCurrentLocation.getLatitude() != 0.0 && mCurrentLocation.getLongitude() != 0.0) {
                setFakeMarker(mCurrentLocation);
            }
        }
    }


    public void onGeoQueryError(DatabaseError error) {
        mMayoApplication.showToast(MapActivity.this, String.valueOf(error));
    }

    /**
     * Perfom method when current marker going outside from current circle
     * Then remove previous fake marker all and create new fake marker
     */
    public void moveMarkerOutsideFromCurrentLocation(String pkey) {
        if (pkey.equals(CommonUtility.getUserId(this))) {
            if (mFakeMarker != null) {
                mFakeMarker.stoptimertask();
            }
            //remove previous circle
            if (mCurrentLocationGroundOverlay != null) {
                mCurrentLocationGroundOverlay.remove();
            }
            if (mCurrentLocation != null) {
                drawCircle(mCurrentLocation);
                setFakeMarker(mCurrentLocation);
            }
            setGeoQuery();
        } else {
            removeUserMarker(pkey);
        }
    }

    private void removeUserMarker(String pKey) {
        HashSet<UserMarker> hashSetUserMarker = new HashSet<>();
        hashSetUserMarker.addAll(mNearByUsers);
        for (UserMarker userMarker : hashSetUserMarker) {
            if (userMarker.getKey().equals(pKey)) {
                userMarker.getMarker().remove();
                break;
            }
        }
        mNearByUsers = hashSetUserMarker;
    }

    public void disableLocationDialogView(final int val) {
        if (mDialog == null) {
            mDialog = CommonUtility.showCustomDialog(this, R.layout.location_disable_dialog_view);
        }
        if (mDialog != null) {
            Button NotNowButton = (Button) mDialog.findViewById(R.id.notnowbutton);
            Button SettingButton = (Button) mDialog.findViewById(R.id.settingsbutton);
            NotNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
            SettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (val == Constants.PermissionDialog.AppPermissionDialog.ordinal()) {
                        CommonUtility.goToSettings(MapActivity.this);
                    } else {
                        CommonUtility.goToSettingsForGpsLocation(MapActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View pView, int pPosition, String pMessage) {
        int Type = mMapDataModels.get(pPosition).getFakeCardPosition();
        Constants.CardType cardType = Constants.CardType.values()[Type];
        switch (cardType) {
            case POST:
                switch (pView.getId()) {
                    case R.id.messageIcon:
                        openChatMessageView(pMessage, false, pPosition);
                        break;
                    case R.id.textbutton:
                        if (!pMessage.equals(Constants.sConstantEmptyString)) {
                            getFirebaseInstance();
                            String startColor = mMapDataModels.get(pPosition).getGradientColor().getStartColor().substring(1);
                            String endColor = mMapDataModels.get(pPosition).getGradientColor().getEndColor().substring(1);
                            Task task = mFirebaseDatabase.setTask(pMessage, this, startColor, endColor);
                            mhelpMessageList.clear();
                            mFirebaseDatabase.writeNewTask(task.getTaskID(), task);
                            mFirebaseDatabase.writeNewChannelForCurrentTask(task.getTaskID());
                            mFirebaseDatabase.writeTaskCreatedInUserNode(CommonUtility.getUserId(this), task.getTaskID());
                            mFirebaseDatabase.setListenerForEnteringTask(task.getTaskID());
                            mFirebaseDatabase.getAllMessagesOfTaskEnteredByUser(task.getTaskID(), this);
                            mFirebaseDatabase.sendPushNotificationToNearbyUsers(mNearByUsers,task.getTaskID());
                            TaskLatLng taskLatLng = new TaskLatLng();
                            taskLatLng.setTask(task);
                            TaskLocations taskLocations = new TaskLocations();
                            taskLocations.setLongitude(mCurrentLocation.getLongitude());
                            taskLocations.setLatitude(mCurrentLocation.getLatitude());
                            taskLocations.setKey(task.getTaskID());
                            taskLatLng.setTaskLocations(taskLocations);
                            mMapDataModels.get(pPosition).setTaskLatLng(taskLatLng);
                            sendDataToFirebaseOfTaskLocation(mCurrentLocation, task.getTaskID());
                            // user applied for task
                            CommonUtility.setTaskApplied(true, MapActivity.this);
                            //save Task location
                            CommonUtility.setTaskLocation(mCurrentLocation, MapActivity.this);
                            // save task data
                            CommonUtility.setTaskData(task, MapActivity.this);
                            mFirebaseDatabase.setUpdateTimeOfCurrentTask(task.getTaskID());
                        }
                        break;
                    case R.id.doneIcon:
                        if (mMayoApplication.isConnected()) {
                            setParentAnmationOfQuest(R.anim.slide_up);
                            setParentQuestButton(View.VISIBLE);
                        } else {
                            showInternetConnectionDialog();
                        }
                        break;
                }
                break;
            case FAKECARDTWO:
                openChatMessageView(Constants.sConstantEmptyString, false, pPosition);
                break;
            case DEFAULT:
                switch (pView.getId()) {
                    case R.id.expiryImageView:
                        openChatMessageView(pMessage, true, pPosition);
                        break;
                    case R.id.canHelped:
                        openChatMessageView(pMessage, false, pPosition);
                        break;
                }
                break;
        }
    }

    public void scheduleTaskTimer(int pTaskExpiryTime, Task pTask) {
        if (mTaskTimer != null) {
            mTaskTimer.stoptimertask();
        }
        if (pTaskExpiryTime == 0) {
            updateTaskData(getResources().getString(R.string.STATUS_FOR_TIME_EXPIRED), pTask);
            mMapViewPagerAdapter.setPostMessageView();
            return;
        }
        mTaskTimer = new TaskTimer(MapActivity.this, pTaskExpiryTime, mMapViewPagerAdapter);
        mTaskTimer.startTimer();
    }

    public void showInternetConnectionDialog() {
        final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.internet_connection);
        if (dialog != null) {
            dialog.findViewById(R.id.noConnectionButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Click(R.id.cancelButton)
    public void onClickOfCancel() {
        setParentQuestButton(View.GONE);
        setParentAnmationOfQuest(R.anim.slide_down);
    }

    @Click(R.id.questButton)
    public void onClockOfQuestButton() {
        setParentQuestButton(View.GONE);
        mMapViewPagerAdapter.setPostMessageView();
        setParentAnmationOfQuest(R.anim.slide_down);
        final Dialog dialog = CommonUtility.showCustomDialog(MapActivity.this, R.layout.thanks_dialog);
        Task task = null;
        if (dialog != null) {
            if (CommonUtility.getTaskApplied(MapActivity.this)) {
                CommonUtility.setTaskApplied(false, MapActivity.this);
                task = CommonUtility.getTaskData(this);
                if (mTaskTimer != null) {
                    mTaskTimer.stoptimertask();
                }
                if (mGeoFencing != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    mGeoFencing.stopGeoFenceMonitoring(mGoogleApiClient);
                }
                isThanksDialogOpen = true;
                Drawable drawable = CommonUtility.getGradientDrawable("#" + task.getEndColor(), "#" + task.getStartColor(), this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    dialog.findViewById(R.id.thanksDialogBackground).setBackground(drawable);
                } else {
                    dialog.findViewById(R.id.thanksDialogBackground).setBackgroundDrawable(drawable);
                }
            }
            mHelpRecyclerView = (RecyclerView) dialog.findViewById(R.id.helpPersonsRecyclerView);
            setThanksDialog(dialog);
            removeMessageListener();
        }
    }

    private void setThanksDialog(final Dialog pDialog) {
        getFirebaseInstance();
        mMessageList = mFirebaseDatabase.getLastFiveMessagesFromMessagesList();
        mThanksTextView = (TextView) pDialog.findViewById(R.id.thanks);
        TextView noThanksTextView = (TextView) pDialog.findViewById(R.id.no_one_helped);
        pDialog.findViewById(R.id.lineThanks).setAlpha(Constants.sTransparencyLevelFade);
        if (mHelpRecyclerView != null) {
            ThanksChatAdapter helpChatAdapter = new ThanksChatAdapter(mMessageList, this, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mHelpRecyclerView.setLayoutManager(layoutManager);
            mHelpRecyclerView.setAdapter(helpChatAdapter);
        }
        mThanksTextView.setAlpha(Constants.sTransparencyLevelFade);
        noThanksTextView.setAlpha(Constants.sNonTransparencyLevel);
        mThanksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThanksTextView.getAlpha() == Constants.sNonTransparencyLevel) {
                    if (mhelpMessageList != null && mhelpMessageList.size() > 0) {
                        Task task = CommonUtility.getTaskData(MapActivity.this);
                        updateTaskData(getResources().getString(R.string.STATUS_FOR_THANKED), task);
                        pDialog.dismiss();
                    }
                }
            }
        });
        noThanksTextView.findViewById(R.id.no_one_helped).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mhelpMessageList != null && mhelpMessageList.size() == 0) {
                    Task task = CommonUtility.getTaskData(MapActivity.this);
                    updateTaskData(getResources().getString(R.string.STATUS_FOR_NOT_HELPED), task);
                }
                pDialog.dismiss();
            }
        });
    }

    private void removeMessageListener() {
        getFirebaseInstance();
        mFirebaseDatabase.removeGetAllMessagesListener();
    }

    public void updateTaskData(String pMessage, Task task) {
        //task will complete when expired or done
        updateTask(true, pMessage, task.isUserMovedOutside());
        task.setCompleted(true);
        CommonUtility.setTaskData(task, MapActivity.this);
        TaskLatLng taskLatLng = mCardsDataModel.setTaskLatlngModel(task, CommonUtility.getTaskLocation(MapActivity.this));
        MapDataModel mapDataModel = mCardsDataModel.getMapModelData(taskLatLng);
        mMapDataModels.add(mapDataModel);
        mCardsDataModel.setViewPagerAdapter(mMapViewPagerAdapter);
        mCardsDataModel.sortExpiredCardViewList(mMapDataModels);
        CommonUtility.setTaskApplied(false, this);
        if (mMapDataModels.size() > 0 && mMapDataModels.get(0).getCardLatlng() != null &&
                mMapDataModels.get(0).getCardLatlng().getMarker() != null) {
            mMapDataModels.get(0).getCardLatlng().getMarker().remove();
            mMapDataModels.get(0).getCardLatlng().setMarker(null);
        }

        if (pMessage.equals(getResources().getString(R.string.STATUS_FOR_TIME_EXPIRED))) {
            showLocalNotification(1);
        }
        if (task.isRecentActivity() && !isThanksDialogOpen) {
            Dialog dialog = CommonUtility.showCustomDialog(MapActivity.this, R.layout.thanks_dialog);
            if (dialog != null && !dialog.isShowing()) {
                setThanksDialog(dialog);
            }
        }
    }

    public void showLocalNotification(int pId) {
        NotificationCompat.Builder builder = CommonUtility.notificationBuilder(this,
                getResources().getString(R.string.notification_expired_message), getResources().getString(R.string.notification_help_message));
        Intent notificationIntent = new Intent(this, MapActivity_.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, pId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        mMayoApplication.showLocalNotification(0, builder, this);
    }

    private void setParentQuestButton(int visibility) {
        mParentQuestLayout.setVisibility(visibility);
    }

    private void setParentAnmationOfQuest(int pAnimType) {
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), pAnimType);
        mParentQuestLayout.startAnimation(slide_up);
    }

    private void updateTask(boolean pCompleteOrNot, String pCompleteType, boolean pUserMoveOutside) {
        getFirebaseInstance();
        Task task;
        Task taskData = CommonUtility.getTaskData(MapActivity.this);
        if (pCompleteType.equals(getResources().getString(R.string.STATUS_FOR_THANKED))) {
            ArrayList<String> stringArrayList = new ArrayList<>();
            if (mhelpMessageList.size() > 0) {
                for (Message message : mhelpMessageList) {
                    stringArrayList.add(message.getSenderId());
                }
            }
            task = mFirebaseDatabase.updateTaskOnFirebase(pCompleteOrNot, pCompleteType, this, pUserMoveOutside, taskData.isRecentActivity(), stringArrayList);
        } else {
            task = mFirebaseDatabase.updateTaskOnFirebase(pCompleteOrNot, pCompleteType, this, pUserMoveOutside, taskData.isRecentActivity(), new ArrayList<String>());
        }
        mFirebaseDatabase.updateTask(task.getTaskID(), task);
    }

    public void userMovedAway() {
        if (CommonUtility.getTaskApplied(this)) {
            Task task = CommonUtility.getTaskData(this);
            if (task != null) {
                if (!task.getTaskDescription().equals(Constants.sConstantEmptyString) &&
                        !task.isUserMovedOutside()) {
                    task.setUserMovedOutside(true);
                    CommonUtility.setTaskData(task, this);
                    if (task.isRecentActivity()) {
                        updateTask(false, Constants.sConstantEmptyString, true);
                    } else {
                        updateTask(true, getResources().getString(R.string.STATUS_FOR_MOVING_OUT), true);
                        if (mTaskTimer != null) {
                            mTaskTimer.stoptimertask();
                        }
                        CommonUtility.setTaskApplied(true, this);
                        if (mMapViewPagerAdapter != null) {
                            mMapViewPagerAdapter.setPostMessageView();
                        }
                    }
                }
            }
        }
    }

    public void openChatMessageView(String pMessage, boolean pExpiredCard, int pPosition) {
        if (pMessage.equals(Constants.sConstantEmptyString)) {
            ChatActivity_.intent(MapActivity.this).start();
        } else {
            ChatActivity_.intent(MapActivity.this).extra(Constants.sPostMessage, pMessage)
                    .extra(Constants.sQuestMessageShow, pExpiredCard).extra(Constants.sCardsData, mMapDataModels.get(pPosition)).start();
        }
        overridePendingTransition(R.anim.slide_out_left, R.anim.push_down_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public void setRecentActivity(boolean pVal) {
        for (int i = 0; i < mMapDataModels.size(); i++) {
            if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.POST.getValue()) {
                mMapDataModels.get(i).getTaskLatLng().getTask().setRecentActivity(pVal);
                break;
            }
        }
    }


    private void registerReceiver() {
        LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this);
        mReceiver = new GoogleReceiver(this);
        lbc.registerReceiver(mReceiver, new IntentFilter(Constants.BroadCastReceiver.sBroadCastName));
    }

    class GoogleReceiver extends BroadcastReceiver {

        MapActivity mActivity;

        public GoogleReceiver(Activity activity) {
            mActivity = (MapActivity) activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            userMovedAway();
            if (mGeoFencing != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGeoFencing.stopGeoFenceMonitoring(mGoogleApiClient);
            }
        }
    }

}
