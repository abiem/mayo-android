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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.mayo.firebase.database.FirebaseDatabase;
import com.mayo.interfaces.LocationUpdationInterface;
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.GradientColor;
import com.mayo.models.MapDataModel;
import com.mayo.models.Task;
import com.mayo.viewclasses.CardColor;
import com.mayo.viewclasses.CustomViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

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
    GifView mImageHandsViewOnMap;

    @ViewById(R.id.rotateImageOnMapView)
    ImageView mRotateImageOnMapView;

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Double mCurrentLat, mCurrentLng;
    private Circle mCurrentLocationCircle;
    private Marker mCurrentLocationMarker;

    private BackgroundLocationService mBackgroundLocationService;
    ArrayList<MapDataModel> mMapDataModels;
    Dialog mDialog;
    boolean scrollStarted = true, checkDirection;
    int positionNew;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private Location mCurrentLocation;

    @AfterViews
    protected void init() {
        mMayoApplication.setActivity(this);
        mMapView.onCreate(null);
        mImageHandsViewOnMap.setGifResource(R.drawable.thanks);
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
        FirebaseDatabase firebaseDatabase = new FirebaseDatabase();
        mGeoFire = firebaseDatabase.startLocationUpdatesWithGeoFire();
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
        mMapDataModels = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MapDataModel mapDataModel = new MapDataModel();
            int color = CardColor.generateRandomColor();
            GradientColor gradientColor = new GradientColor();
            gradientColor.setStartColor(CardColor.choices[color][1]);
            gradientColor.setEndColor(CardColor.choices[color][0]);
            mapDataModel.setGradientColor(gradientColor);
            mapDataModel.setFakeCard(false);
            if (i > 0 & i < 4) {
                mapDataModel.setFakeCard(true);
            }
            Drawable drawable = CommonUtility.getGradientDrawable(CardColor.choices[color][0], CardColor.choices[color][1], this);
            mapDataModel.setBackgroundView(drawable);
            Constants.CardType cardType = Constants.CardType.values()[i];
            switch (cardType) {
                case POST:
                    mapDataModel.setTextMessage(getResources().getString(R.string.help_message));
                    mapDataModel.setButtonMessage(getResources().getString(R.string.post));
                    mapDataModel.setFakeCardPosition(Constants.CardType.POST.getValue());
                    break;
                case FAKECARDONE:
                    mapDataModel.setTextMessage(getResources().getString(R.string.helping_message));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDONE.getValue());
                    if (CommonUtility.getFakeCardOne(MapActivity.this)) {
                        continue;
                    }
                    break;
                case FAKECARDTWO:
                    mapDataModel.setTextMessage(getResources().getString(R.string.ai_message));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTWO.getValue());
                    if (CommonUtility.getFakeCardTwo(MapActivity.this)) {
                        continue;
                    }
                    break;
                case FAKECARDTHREE:
                    mapDataModel.setTextMessage(getResources().getString(R.string.need_help));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTHREE.getValue());
                    if (CommonUtility.getFakeCardThree(MapActivity.this)) {
                        continue;
                    }
                    break;
            }

            mMapDataModels.add(mapDataModel);
        }

//        for (int i = 0; i < 3; i++) {
//            MapDataModel mapDataModel = new MapDataModel();
//            GradientColor gradientColor = new GradientColor();
//            gradientColor.setStartColor(CardColor.expireCard[0]);
//            gradientColor.setEndColor(CardColor.expireCard[1]);
//            mapDataModel.setGradientColor(gradientColor);
//            mapDataModel.setFakeCard(false);
//            mapDataModel.setFakeCardPosition(Constants.CardType.DEFAULT.getValue());
//            Drawable drawable = CommonUtility.getGradientDrawable(CardColor.expireCard[0], CardColor.expireCard[1], this);
//            mapDataModel.setBackgroundView(drawable);
//            Constants.CardType cardType = Constants.CardType.values()[4];
//            switch (cardType) {
//                case DEFAULT:
//                    mapDataModel.setTextMessage(getResources().getString(R.string.expired_card));
//            }
//            mMapDataModels.add(mapDataModel);
//        }

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
            mImageHandsViewOnMap.play();
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
            mImageHandsViewOnMap.pause();
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
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(10f);
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
                        sendDataToFirebase(location);
                        if (mGeoQuery == null) {
                            setGeoQuery();
                        }
                    }
                }
            }
        });
    }

    private void setCurrentLocation(Location location) {
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLat = location.getLatitude();
        mCurrentLng = location.getLongitude();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, Constants.sKeyCameraZoom);
        CameraPosition cameraPosition = new CameraPosition(ll, 45, 45, 45);
        update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.animateCamera(update);
        if (mCurrentLocationCircle == null) {
            drawCircle(location);
        }
        if (mCurrentLocationMarker == null) {
            addCurrentLocationMarker(location);
        }
    }

    private void drawCircle(Location location) {
        mCurrentLocationCircle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(Constants.sKeyForMapRadius)
                .strokeColor(ContextCompat.getColor(MapActivity.this, R.color.transparent))
                .fillColor(ContextCompat.getColor(MapActivity.this, R.color.colorTransparentBlue)));
    }

    private void addCurrentLocationMarker(Location location) {
        Bitmap bitmap = CommonUtility.drawableToBitmap(ContextCompat.getDrawable(MapActivity.this, R.drawable.location_circle));
        BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        MarkerOptions currentLocationMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(getResources().getString(R.string.my_location))
                .icon(currentLocationIcon);
        mCurrentLocationMarker = mGoogleMap.addMarker(currentLocationMarker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void updateLocation(Location location) {
        sendDataToFirebase(location);
    }

    /**
     * send current location data to firebase
     *
     * @param location
     */
    private void sendDataToFirebase(Location location) {
        mCurrentLocation = location;
        mGeoFire.setLocation(CommonUtility.getUserId(MapActivity.this),
                new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (mCurrentLocationMarker != null) {
                            mCurrentLocationMarker.remove();
                        }
                        addCurrentLocationMarker(mCurrentLocation);
                        if (mGoogleMap != null)
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()), Constants.sKeyCameraZoom));
                    }
                });
    }

    /**
     * perform GeoQuery on every 200 m radius
     */
    private void setGeoQuery() {
        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude()), Constants.sKeyForMapRadius);
        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                mMayoApplication.showToast(MapActivity.this, String.valueOf(location));
            }

            @Override
            public void onKeyExited(String key) {
                moveMarkerOutsideFromCurrentLocation();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                mMayoApplication.showToast(MapActivity.this, String.valueOf(error));
            }
        });
    }

    /**
     * Perfom method when current marker going outside from current circle
     */
    private void moveMarkerOutsideFromCurrentLocation() {
        if (mCurrentLocationCircle != null) {
            mCurrentLocationCircle.remove();
        }
        if (mCurrentLocation != null) {
            drawCircle(mCurrentLocation);
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
                    case R.id.textbutton:
                        if (!pMessage.equals(Constants.sConstantString)) {
                            Task task = setNewTask(pPosition, pMessage);
                            FirebaseDatabase firebaseDatabase = new FirebaseDatabase();
                            firebaseDatabase.writeNewTask(task.getTaskID(), task);
                        }
                        break;
                }
                break;
            case FAKECARDTWO:
                openChatMessageView();
                break;
        }
    }

    private Task setNewTask(int pPosition, String pMessage) {
        Task task = new Task();
        task.setCreatedby(CommonUtility.getUserId(this));
        task.setTaskID(CommonUtility.convertLocalTimeToUTC());
        task.setHelpedBy(Constants.sConstantString);
        task.setTimeCreated(CommonUtility.getLocalTime()); //this is time when we create task
        task.setCompleted(false);
        task.setTaskDescription(pMessage);
        task.setTimeUpdated(CommonUtility.getLocalTime()); //this is updating time but first time we showing create task time
        task.setUserMovedOutside(false);
        task.setRecentActivity(false);
        task.setStartColor(mMapDataModels.get(pPosition).getGradientColor().getStartColor().substring(1));
        task.setEndColor(mMapDataModels.get(pPosition).getGradientColor().getEndColor().substring(1));
        task.setCompleteType("");
        return task;
    }

    //this is for fake card
    public void openChatMessageView() {
        ChatActivity_.intent(MapActivity.this).start();
        overridePendingTransition(R.anim.slide_out_left, R.anim.push_down_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

}
