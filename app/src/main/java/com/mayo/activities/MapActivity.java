package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.IntroViewPagerAdapter;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.backgroundservice.BackgroundLocationService;
import com.mayo.firebase.database.FirebaseDatabase;
import com.mayo.interfaces.ClickListener;
import com.mayo.interfaces.LocationUpdationInterface;
import com.mayo.interfaces.ViewClickListener;
import com.mayo.models.GradientColor;
import com.mayo.models.MapDataModel;
import com.mayo.models.Task;
import com.mayo.models.TutorialModel;
import com.mayo.viewclasses.CardColor;
import com.mayo.viewclasses.CustomViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static com.mayo.Utility.CommonUtility.isLocationEnabled;

@SuppressLint("Registered")
@EActivity(R.layout.activity_map)
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationUpdationInterface, ViewClickListener {

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

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Double mCurrentLat, mCurrentLng;
    private Circle mCurrentLocationCircle;
    private Marker mCurrentLocationMarker;

    private BackgroundLocationService mBackgroundLocationService;
    ArrayList<MapDataModel> mMapDataModels;

    @AfterViews
    protected void init() {
        mMayoApplication.setActivity(this);
        mMapView.onCreate(null);
        setDataModel();
        setViewPager();
        scrollViewPager();
        if (CommonUtility.googleServicesAvailable(this)) {
            mMapView.getMapAsync(MapActivity.this);
        } else {
            mMayoApplication.showToast(this, getResources().getString(R.string.notsupported));
        }
        mCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPointView();
            }
        });
    }

    private void checkPermissions() {
        String permission[] = CommonUtility.checkPermissions();
        for (String aPermission : permission) {
            if (ContextCompat.checkSelfPermission(this, aPermission) != PackageManager.PERMISSION_GRANTED) {
                locationNotEnabled();
                disableLocationDialogView();
                break;
            }
        }
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

        }
    }

    private void scrollViewPager() {
        mViewPagerMap.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
//                        if (CommonUtility.getSoftKeyBoardState(MapActivity.this)) {
//                            CommonUtility.showSoftKeyBoard(MapActivity.this);
//                        }
                        break;
                    default:
                        CommonUtility.hideSoftKeyboard(MapActivity.this);

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
            Drawable drawable = CommonUtility.getGradientDrawable(CardColor.choices[color][0], CardColor.choices[color][1], this);
            mapDataModel.setBackgroundView(drawable);
            mMapDataModels.add(mapDataModel);
        }

        //this is for expiry card
        MapDataModel mapDataModel = new MapDataModel();
        Drawable drawable = CommonUtility.getGradientDrawable(CardColor.expireCard[0], CardColor.expireCard[1], this);
        mapDataModel.setBackgroundView(drawable);
        GradientColor gradientColor = new GradientColor();
        gradientColor.setStartColor(CardColor.expireCard[1]);
        gradientColor.setEndColor(CardColor.expireCard[0]);
        mapDataModel.setGradientColor(gradientColor);
        mMapDataModels.add(mapDataModel);
    }

    private void setViewPager() {
        mViewPagerMap.setPagingEnabled(true);
        mViewPagerMap.setClipToPadding(false);
        mViewPagerMap.setPadding(64, 80, 64, 80);
        mViewPagerMap.setPageMargin(24);
        mViewPagerMap.setAdapter(new MapViewPagerAdapter(this, mMapDataModels, this, mViewPagerMap));
        mViewPagerMap.setCurrentItem(1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
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
            disableLocationDialogView();
            return;
        }
        checkPermissions();
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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0.0, 0.0), 14.0f));
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
    public void updateLocation() {

    }

    private void disableLocationDialogView() {
        final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.location_disable_dialog_view);
        if (dialog != null) {
            Button NotNowButton = (Button) dialog.findViewById(R.id.notnowbutton);
            Button SettingButton = (Button) dialog.findViewById(R.id.settingsbutton);
            NotNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            SettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtility.goToSettings(MapActivity.this);
                }
            });
        }
    }


    @Override
    public void onClick(View pView, int pPosition,String pMessage) {
        switch (pPosition) {
            case 0:
                switch (pView.getId()) {
                    case R.id.viewText:
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        CommonUtility.setSoftKeyBoardState(true, this);
                        break;
                    case R.id.textbutton:
                        Task task = setNewTask(pPosition,pMessage);
                        CommonUtility.getUserId(this);
                        FirebaseDatabase firebaseDatabase = new FirebaseDatabase();
                        firebaseDatabase.writeNewTask(task);
                        break;
                    case R.id.imagebutton:
                        break;
                }
                break;
        }
    }

    private Task setNewTask(int pPosition,String pMessage) {
        Task task = new Task();
        task.setCreatedby(CommonUtility.getUserId(this));
        task.setTaskID(CommonUtility.convertLocalTimeToUTC());
        task.setHelpedBy(Constants.sConstantString);
        task.setTimeCreated(CommonUtility.getLocalTime()); //this is time when we create task
        task.setCompleted(false);
        task.setTimeUpdated(CommonUtility.getLocalTime()); //this is updated but first time when we create task
        task.setUserMovedOutside(false);
        task.setRecentActivity(false);
        task.setStartColor(mMapDataModels.get(pPosition).getGradientColor().getStartColor().substring(1));
        task.setEndColor(mMapDataModels.get(pPosition).getGradientColor().getEndColor().substring(1));
        task.setCompleteType("");
        task.setTaskDescription(pMessage);
        return task;
    }
}
