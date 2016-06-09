package com.github.nkzawa.socketio.androidchat;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private boolean mRequestingLocationUpdates;
    protected Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    private LocationTracker mLocationTracker;

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
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mLocationTracker.mLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLocationTracker.mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }
        //updateUi();
    }

    //private void updateUi() {

    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            // Create a Google API instance
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mLocationTracker = new LocationTracker();
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        /**
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Proximity Notifier", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("NULL")
        );

        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
        */
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /**
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Proximity Notifier", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(null)
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        */
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationTracker.stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() /*&& mRequestingLocationUpdates*/) {
            mLocationTracker.startLocationUpdates();
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
        mLocationTracker.handleLocationChange(location);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mLocationTracker.mLocation);
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