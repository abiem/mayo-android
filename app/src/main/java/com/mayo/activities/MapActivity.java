package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.github.sahasbhop.apngview.ApngDrawable;
import com.github.sahasbhop.apngview.ApngImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.mayo.application.MayoApplication;
import com.mayo.backgroundservice.BackgroundLocationService;
import com.mayo.classes.FakeDataModel;
import com.mayo.firebase.database.FirebaseDatabase;
import com.mayo.interfaces.LocationUpdationInterface;
import com.mayo.interfaces.ViewClickListener;
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

import java.util.ArrayList;
import java.util.HashSet;

import static com.mayo.Utility.CommonUtility.isLocationEnabled;

@SuppressLint("Registered")
@EActivity(R.layout.activity_map)
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationUpdationInterface, ViewClickListener, GoogleMap.OnMapClickListener {

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

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Double mCurrentLat, mCurrentLng;
    private GroundOverlay mCurrentLocationGroundOverlay;
    private Marker mCurrentLocationMarker;
    private ArrayList<UserMarker> mFakeUserMarker;

    private BackgroundLocationService mBackgroundLocationService;
    ArrayList<MapDataModel> mMapDataModels;
    Dialog mDialog;
    boolean scrollStarted = true, checkDirection;
    int positionNew;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private GeoFireClass mGeoFireClass;
    private GeoQuery mGeoQuery;
    private Location mCurrentLocation, mTaskLocation;
    private FakeMarker mFakeMarker;
    private ApngDrawable mApngDrawable;
    private HashSet<UsersLocations> mNearByUsers;

    @AfterViews
    protected void init() {
        mMayoApplication.setActivity(this);
        mMapView.onCreate(null);
        mNearByUsers = new HashSet<>();
        ApngImageLoader.getInstance().displayImage("assets://apng/fist_bump_720.png", mImageHandsViewOnMap);
        mFakeUserMarker = new ArrayList<>();
        mRelativeMapLayout.setFitsSystemWindows(true);
        setDataModel();
        setViewPager();
        scrollViewPager();
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

    private void getCurrentLocation() {
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()), Constants.sKeyCameraZoom));
        }
    }

    private void scrollViewPager() {
        mViewPagerMap.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                boolean isGoingToRightPage = position == positionNew;
                if (checkDirection) {
                    if (isGoingToRightPage) {
                        // user is going to the right page
                    } else {
                        // user is going to the left page
                    }
                    checkDirection = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                int Type = mMapDataModels.get(position).getFakeCardPosition();
                Constants.CardType cardType = Constants.CardType.values()[Type];
                if (position != Constants.CardType.POST.getValue()) {
                    if (CommonUtility.getSoftKeyBoardState(MapActivity.this)) {
                        mMayoApplication.hideKeyboard(getCurrentFocus());
                        CommonUtility.setSoftKeyBoardState(false, MapActivity.this);
                    }
                }
                switch (cardType) {
                    case POST:
                        if (CommonUtility.getFakeCardShownOrNot(MapActivity.this)) {
                            for (int i = 0; i < mMapDataModels.size(); i++) {
                                if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTHREE.getValue()) {
                                    mMapViewPagerAdapter.deleteItemFromArrayList(i);
                                    int value = CommonUtility.getPoints(MapActivity.this) + 1;
                                    CommonUtility.setPoints(value, MapActivity.this);
                                    mCountButton.setText(String.valueOf(value));
                                    CommonUtility.setFakeCardThree(true, MapActivity.this);
                                    break;
                                }
                            }
                        }
                        mMapViewPagerAdapter.setCardViewVisible();
                    case FAKECARDONE:
                        break;
                    case FAKECARDTWO:
                        if (CommonUtility.getFakeCardShownOrNot(MapActivity.this)) {
                            for (int i = 0; i < mMapDataModels.size(); i++) {
                                if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDONE.getValue()) {
                                    mMapViewPagerAdapter.deleteItemFromArrayList(i);
                                    int value = CommonUtility.getPoints(MapActivity.this) + 1;
                                    CommonUtility.setPoints(value, MapActivity.this);
                                    mCountButton.setText(String.valueOf(value));
                                    CommonUtility.setFakeCardOne(true, MapActivity.this);
                                    mViewPagerMap.setCurrentItem(1);
                                    break;
                                }
                            }
                        }
                        break;
                }
                positionNew = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    scrollStarted = true;
//                    checkDirection = true;
//                } else {
//                    scrollStarted = false;
//                }
            }
        });
    }

    private void setDataModel() {
        FakeDataModel fakeDataModel = new FakeDataModel(this);
        mMapDataModels = fakeDataModel.getDataModel();
    }

    private void setViewPager() {
        mViewPagerMap.setPagingEnabled(true);
        mViewPagerMap.setClipToPadding(false);
        mViewPagerMap.setPadding(Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding,
                Constants.CardPaddingValues.sLeftRightPadding, Constants.CardPaddingValues.sTopBottomPadding);
        mViewPagerMap.setPageMargin(Constants.CardMarginSetValues.sMarginValue);
        mMapViewPagerAdapter = new MapViewPagerAdapter(this, mMapDataModels, this, mViewPagerMap, this, mMayoApplication);
        mViewPagerMap.setAdapter(mMapViewPagerAdapter);
        if (mMapDataModels.size() > 1) {
            mViewPagerMap.setCurrentItem(1);
        }
        positionNew = mViewPagerMap.getCurrentItem();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (CommonUtility.getHandsAnimationShownOnMap(MapActivity.this)) {
            mImageHandsViewOnMap.setVisibility(View.VISIBLE);
            mApngDrawable = ApngDrawable.getFromView(mImageHandsViewOnMap);
            if (mApngDrawable == null)
                return;
            mApngDrawable.setNumPlays(0);
            mApngDrawable.start();
            mRotateImageOnMapView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(MapActivity.this, R.anim.rotate);
            mRotateImageOnMapView.startAnimation(animation);
            CommonUtility.setHandsAnimationShownOnMap(false, MapActivity.this);
            mRelativeMapLayout.setFitsSystemWindows(false);
            new CountDown(4000, 1000);
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
        mMapViewPagerAdapter.setPostCardText();
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
            if (mApngDrawable.isRunning()) {
                mApngDrawable.stop(); // Stop animation
            }
            mImageHandsViewOnMap.setVisibility(View.GONE);
            if (mMapViewPagerAdapter != null) {
                for (int i = 0; i < mMapDataModels.size(); i++) {
                    if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.FAKECARDTWO.getValue()) {
                        mMapViewPagerAdapter.deleteItemFromArrayList(i);
                        int value = CommonUtility.getPoints(MapActivity.this) + 1;
                        CommonUtility.setPoints(value, MapActivity.this);
                        mCountButton.setText(String.valueOf(value));
                        CommonUtility.setFakeCardTwo(true, MapActivity.this);
                        break;
                    }
                }
            }
            mRelativeMapLayout.setFitsSystemWindows(true);
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
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0.0, 0.0), 14.0f));
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
                        setCurrentLocation(location);
                        sendDataToFirebaseOfUserLocation(location);
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
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLat = location.getLatitude();
        mCurrentLng = location.getLongitude();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, Constants.sKeyCameraZoom);
        CameraPosition cameraPosition = new CameraPosition(ll, 45, 45, 75);
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
            addFakeUserMaker(i + 1, location);
        }
        if (mFakeUserMarker.size() > 0) {
            CommonUtility.setFakeMarkerShown(true, MapActivity.this);
            mFakeMarker = new FakeMarker(MapActivity.this, mFakeUserMarker);
            mFakeMarker.startTimer();
        }
    }

    private void addFakeUserMaker(int count, Location location) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(MapActivity.this, R.drawable.location_fake_users_circle));
        BitmapDescriptor fakeLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        double lat, lng;
        Constants.FakeMarkerValues fakeUserMaker = Constants.FakeMarkerValues.values()[count];
        int latlngContainNumber = FakeMarker.generateRandomLocationOfFakeUser();
        lat = FakeMarker.fakeUserChoices[latlngContainNumber][0];
        lng = FakeMarker.fakeUserChoices[latlngContainNumber][1];
        LatLng latLng = new LatLng(location.getLatitude() + lat, location.getLongitude() + lng);
        MarkerOptions fakeMarker = new MarkerOptions().position(latLng)
                .icon(fakeLocationIcon);
        setFakeUserModel(fakeMarker, latLng);
    }

    public void removeFakeMarkerAccodingToTime(int pIndex) {
        mFakeUserMarker.get(pIndex).getMarker().remove();
        mFakeUserMarker.remove(pIndex);
        if (mFakeUserMarker.size() == 0) {
            CommonUtility.setFakeMarkerShown(true, MapActivity.this);
            if (mFakeMarker != null) {
                mFakeMarker.stoptimertask();
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
        addCurrentLocationMarker(mCurrentLocation);
        sendDataToFirebaseOfUserLocation(location);
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
        FirebaseDatabase firebaseDatabase = new FirebaseDatabase(this);
        GeoFire geoFire = firebaseDatabase.setTaskLocationWithGeoFire();
        if (mTaskLocation != null) {
            geoFire.setLocation(pTimeStamp,
                    new GeoLocation(mTaskLocation.getLatitude(), mTaskLocation.getLongitude()));
        }
    }

    /**
     * perform GeoQuery on every 200 m radius
     */
    private void setGeoQuery() {
        mNearByUsers.clear();
        mGeoQuery = mGeoFireClass.setGeoQuery(mCurrentLocation);
    }

    public void getNearByUsers(String pKey, GeoLocation pGeoLocation) {
        UsersLocations usersLocations = new UsersLocations();
        usersLocations.setKey(pKey);
        usersLocations.setLatitude(pGeoLocation.latitude);
        usersLocations.setLongitude(pGeoLocation.longitude);
        if (!pKey.equals(CommonUtility.getUserId(this))) {
            mNearByUsers.add(usersLocations);
        }
    }

    public void createMarkerOfRealTimeUsers() {
        FirebaseDatabase firebaseDatabase = new FirebaseDatabase(this);
        HashSet<UsersLocations> userCurrentMatker = new HashSet<>();
        userCurrentMatker.addAll(mNearByUsers);
        for (UsersLocations usersLocations : mNearByUsers) {
            String key = usersLocations.getKey();
            if (usersLocations.getKey().contains("Optional(")) {
                key = key.substring(key.indexOf("(") + 2, key.length() - 2);
                firebaseDatabase.getUsersTimeFromFirebase(key, userCurrentMatker, usersLocations);
            } else {
                firebaseDatabase.getUsersTimeFromFirebase(key, userCurrentMatker, usersLocations);
            }
        }
    }

    private void showFakeMarker() {
        if (isLocationEnabled(this) && !CommonUtility.getFakeMarkerShown(MapActivity.this)
                && mCurrentLocation != null) {
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
    public void moveMarkerOutsideFromCurrentLocation() {
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

    }

    private void disableLocationDialogView(final int val) {
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
                        openChatMessageView(pMessage);
                        break;
                    case R.id.textbutton:
                        if (!pMessage.equals(Constants.sConstantString)) {
                            FirebaseDatabase firebaseDatabase = new FirebaseDatabase(this);
                            String startColor = mMapDataModels.get(pPosition).getGradientColor().getStartColor().substring(1);
                            String endColor = mMapDataModels.get(pPosition).getGradientColor().getEndColor().substring(1);
                            Task task = firebaseDatabase.setTask(pMessage, this, startColor, endColor);
                            firebaseDatabase.writeNewTask(task.getTaskID(), task);
                            sendDataToFirebaseOfTaskLocation(mCurrentLocation, task.getTaskID());
                            // user applied for task
                            CommonUtility.setTaskApplied(true, MapActivity.this);
                            // save task data
                            CommonUtility.setTaskData(task, MapActivity.this);
                        }
                        break;
                    case R.id.doneIcon:
                        setParentAnmationOfQuest(R.anim.slide_up);
                        setParentQuestButton(View.VISIBLE);
                        break;
                }
                break;
            case FAKECARDTWO:
                openChatMessageView(Constants.sConstantString);
                break;
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
        if (dialog != null) {
            if (CommonUtility.getTaskApplied(MapActivity.this)) {
                CommonUtility.setTaskApplied(false, MapActivity.this);
                Task task = CommonUtility.getTaskData(this);
                Drawable drawable = CommonUtility.getGradientDrawable("#" + task.getEndColor(), "#" + task.getStartColor(), this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    dialog.findViewById(R.id.thanksDialogBackground).setBackground(drawable);
                } else {
                    dialog.findViewById(R.id.thanksDialogBackground).setBackgroundDrawable(drawable);
                }
            }
            dialog.findViewById(R.id.thanks).setAlpha(Constants.sTransparencyLevelFade);
            dialog.findViewById(R.id.lineThanks).setAlpha(Constants.sTransparencyLevelFade);
            dialog.findViewById(R.id.thanks).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            dialog.findViewById(R.id.no_one_helped).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    updateTask(true, getResources().getString(R.string.STATUS_FOR_NOT_HELPED));
                }
            });
        }
    }

    private void setParentQuestButton(int visibility) {
        mParentQuestLayout.setVisibility(visibility);
    }

    private void setParentAnmationOfQuest(int pAnimType) {
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), pAnimType);
        mParentQuestLayout.startAnimation(slide_up);
    }

    private void updateTask(boolean pCompleteOrNot, String pCompleteType) {
        FirebaseDatabase firebaseDatabase = new FirebaseDatabase(this);
        Task task = firebaseDatabase.updateTaskOnFirebase(pCompleteOrNot, pCompleteType, this);
        firebaseDatabase.updateTask(task.getTaskID(), task);
    }

    //this is for fake card
    public void openChatMessageView(String pMessage) {
        if (pMessage.equals(Constants.sConstantString)) {
            ChatActivity_.intent(MapActivity.this).start();
        } else {
            ChatActivity_.intent(MapActivity.this).extra(Constants.sPostMessage, pMessage).start();
        }
        overridePendingTransition(R.anim.slide_out_left, R.anim.push_down_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

}
