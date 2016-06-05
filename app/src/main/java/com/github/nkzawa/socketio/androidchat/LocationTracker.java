package com.github.nkzawa.socketio.androidchat;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import io.socket.client.Socket;

/**
 * Created by harish on 29/05/16.
 */
public class LocationTracker extends Service implements LocationListener {

    private double lat, lng;
    private Location location;
    private boolean GPSEnabled = false, canGetLocation = false, networkEnabled = false;

    protected LocationManager lmanager;
    private final Context mContext;
    private static final long DISTANCE_UPDATE = 1;          // 1 meter
    private static final long TIME_UPDATE = 1000 * 60 * 1;  // 1 minute

    public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onLocationChanged(Location location) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Check for the best network provider
     * @return (boolean) LocationTracker::canGetLocation
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Returns the location using available resources
     * @return Location
     */
    public Location getLocation() {
        try {
            lmanager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            GPSEnabled = lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = lmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!(GPSEnabled || networkEnabled)) {
                // No way to get location
            } else {
                this.canGetLocation = true;
                if (GPSEnabled)     location = getLocationFromGPS();
                if (networkEnabled) location = getLocationFromNetwork();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch device location");
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Emitt current latitude and longitude to the server
     */
    public void sendLocation() {
        Socket socket;
        MainApplication app = new MainApplication();
        if (app.getSocketConnection().isConnected()) {
            socket =  app.getSocketConnection().getSocket();
            socket.emit("Latitude", getLatitude());
            socket.emit("Longitude", getLongitude());
        } else {
            System.err.println("Connection not found");
        }
    }

    /**
     * Use GPS to return location
     * @return Location
     */
    public Location getLocationFromGPS() {
        lmanager.requestLocationUpdates(lmanager.NETWORK_PROVIDER, TIME_UPDATE, DISTANCE_UPDATE, this);
        Log.d("GPS Enabled", "GPS Enabled");
        if (lmanager != null) {
            location = lmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        }
        return location;
    }

    /**
     * Use the Network provider to return location
     * @return Location
     */
    public Location getLocationFromNetwork() {
        lmanager.requestLocationUpdates(lmanager.GPS_PROVIDER, TIME_UPDATE, DISTANCE_UPDATE, this);
        Log.d("Network", "Network");
        if (lmanager != null) {
            location = lmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        }
        return location;
    }

    /**
     * Get the current latitude
     * @return Location.latitude
     */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * Get the current longitude
     * @return Location.longitude
     */
    public double getLongitude() {
        if (location != null) {
            lng = location.getLongitude();
        }
        return lng;
    }

    /**
     * Show a dialog prompting the user to enable GPS
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("Would you like to enable GPS ?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
