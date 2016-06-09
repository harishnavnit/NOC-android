package com.github.nkzawa.socketio.androidchat;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Notification;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;


public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private boolean mRequestingLocationUpdates;
    protected Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    private MyLocationTracker mMyLocationTracker;

    private static String   LOCATION_KEY,
                            LAST_UPDATED_TIME_STRING_KEY,
                            REQUESTING_LOCATION_UPDATES_KEY;

    /**
     * Start an activity from an already present Bundle state
     * @param savedInstanceState
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }
        }
        if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
            mMyLocationTracker.mLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        }
        if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
            mMyLocationTracker.mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
        }
        //updateUi();
    }

    //private void updateUi() {

    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateValuesFromBundle(savedInstanceState);

        // Create a Google API instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .enableAutoManage(this, this)
                                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                                .addOnConnectionFailedListener(this)
                                .build();
        mMyLocationTracker = new MyLocationTracker();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Proximity Notifier", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(null)
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Proximity Notifier", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(null)
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMyLocationTracker.stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() /*&& mRequestingLocationUpdates*/) {
            mMyLocationTracker.startLocationUpdates();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        System.out.println("Connection to Google APIs failed");
        // ...
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLocationTracker.handleLocationChange(location);
    }

    public GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    public Location getLastKnownLocation() {
        return mLastLocation;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mMyLocationTracker.mLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mMyLocationTracker.mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
}