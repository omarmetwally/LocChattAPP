package com.myprojects.marco.firechat.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.myprojects.marco.firechat.BaseActivity;
import com.myprojects.marco.firechat.Dependencies;
import com.myprojects.marco.firechat.R;
import com.myprojects.marco.firechat.UserLocation;
import com.myprojects.marco.firechat.main.presenter.MainPresenter;
import com.myprojects.marco.firechat.main.view.MainDisplayer;
import com.myprojects.marco.firechat.navigation.AndroidMainNavigator;

/**
 * Created by marco on 14/08/16.
 */

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private MainPresenter presenter;
    private AndroidMainNavigator navigator;

    FusedLocationProviderClient mFusedLocationClient;
    UserLocation userLocation;
    Location msLocation;
    private static final int REQUEST_LOCATION_CODE_PERMISSION = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainDisplayer mainDisplayer = (MainDisplayer) findViewById(R.id.mainView);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        userLocation = new UserLocation();

        getLocation();












        navigator = new AndroidMainNavigator(this, googleApiClient);
        presenter = new MainPresenter(
                Dependencies.INSTANCE.getLoginService(),
                Dependencies.INSTANCE.getUserService(),
                mainDisplayer,
                Dependencies.INSTANCE.getMainService(),
                Dependencies.INSTANCE.getMessagingService(),
                navigator,
                Dependencies.INSTANCE.getFirebaseToken(),
                this
                );
    }


    private UserLocation getLocation() {
        //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "The Location Permission is needed to show the weather  ", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_CODE_PERMISSION);
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();

                }
            }

        } else {
           // Log.d(TAG, "getLocation: Permission Granted");
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        msLocation = location;
                        userLocation.setLat(location.getLatitude());
                        userLocation.setLon(location.getLongitude());
                        Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();

                     //   mLocationText.setText("http://www.google.com/maps/place/"+msLocation.getLatitude()+","+msLocation.getLongitude());
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                      //  mLocationText.setGravity(Gravity.CENTER_HORIZONTAL);
                      //  mLocationText.setLayoutParams(params);
                       // mLocationText.setTextSize(20);
                        Toast.makeText(MainActivity.this, "http://www.google.com/maps/place/"+msLocation.getLatitude()+","+msLocation.getLongitude(), Toast.LENGTH_SHORT).show();


                    } else {
                      //  mLocationText.setText(R.string.no_location);
                        Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }




        return userLocation;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (!presenter.onBackPressed())
            if (!navigator.onBackPressed()) {
                super.onBackPressed();
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // DO SOMETHING
    }

}

