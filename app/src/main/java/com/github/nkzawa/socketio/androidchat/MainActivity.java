package com.github.nkzawa.socketio.androidchat;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "location-updates";

    protected static Location mLastLocation;
    protected static LocationTracker mLocationTracker;
    protected static GoogleApiClient mGoogleApiClient;
    protected static boolean mRequestingLocationUpdates;

    // Keys for storing activity states in bundle
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";

    // Access to UI widgets
    protected MainFragment mMainFragment;
    protected LoginActivity mLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFragment = new MainFragment();
        mLocationTracker = new LocationTracker();

        mMainFragment.mUsername = "John Doe";

        // Locate UI widgets
        mMainFragment.mLocationDisplay = (TextView) findViewById(R.id.LatLngDisplay);
        mMainFragment.mUserNameDisplay = (TextView) findViewById(R.id.usernameDisplay);
        mMainFragment.mLocationButton = (Button) findViewById(R.id.locationButton);
        mMainFragment.mWebView = (WebView) findViewById(R.id.mapView);

        // Set labels
        //mMainFragment.mUserNameDisplay.setText(mMainFragment.mUsername);
        if (mLocationTracker == null) {
            mMainFragment.mLocationDisplay.setText("Fetching ...");
        } else {
            Location loc = mLocationTracker.getCurrentLocation();
            if (loc != null)
                mMainFragment.mLocationDisplay.setText(loc.getLatitude() + ", " + loc.getLongitude());
        }

        mRequestingLocationUpdates = false;
        mLocationTracker.mLastUpdateTime = "";

        // Update values from Bundle
        updateValuesFromBundle(savedInstanceState);
        mLocationTracker = new LocationTracker();
        buildGoogleApiClient();
    }

    /**
     * Start an activity from an already present Bundle state
     * @param savedInstanceState
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mLocationTracker.mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLocationTracker.mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUi();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building Google API client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationTracker.createLocationRequest();
    }


    private void updateUi() {
        if (mMainFragment.mLocationButtonPressed) {
            if (!mRequestingLocationUpdates) {
                mRequestingLocationUpdates = true;
                mLocationTracker.startLocationUpdates();
            }
        } else {
            if (mRequestingLocationUpdates) {
                mRequestingLocationUpdates = false;
                mLocationTracker.stopLocationUpdates();
            }
        }
        if (mLocationTracker.getCurrentLocation() != null)
            mMainFragment.mLocationDisplay.setText(
                    mLocationTracker.getCurrentLocation().getLatitude() + ", " +
                            mLocationTracker.getCurrentLocation().getLongitude()
            );
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            mLocationTracker.stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            mLocationTracker.startLocationUpdates();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mLocationTracker.mCurrentLocation == null) {
            mLocationTracker.mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLocationTracker.mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUi();
        }
        if (mRequestingLocationUpdates) {
            mLocationTracker.startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        System.err.println("Connection to Google APIs failed");
        // ...
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationTracker.handleLocationChange(location);
        updateUi();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mLocationTracker.mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLocationTracker.mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    public GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    public Location getLastKnownLocation() {
        return mLastLocation;
    }
}