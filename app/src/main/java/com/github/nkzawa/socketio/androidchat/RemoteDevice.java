package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.location.Location;

/**
 * Created by harish on 04/06/16.
 */
public class RemoteDevice {
    private long loginHash;
    private Location userLocation;
    protected LocationTracker mLocationTracker;
    private SocketConnection conn;
    private boolean connectionActive;
    private String username, password;

    /**
     * Default constructor
     */
    RemoteDevice() {
        conn = new SocketConnection();
        connectionActive = conn.isConnected();
        username = "";  password = "";
        loginHash = generateLoginHash(password);
        mLocationTracker = MainActivity.mLocationTracker;
        userLocation = LocationTracker.mCurrentLocation;
    }

    /**
     * Multiple argument constructor
     */
    RemoteDevice(String user, String pass) {
        conn = new SocketConnection();
        connectionActive = conn.isConnected();
        username = user;    password = pass;
        loginHash = generateLoginHash(pass);
        mLocationTracker = MainActivity.mLocationTracker;
        userLocation = LocationTracker.mCurrentLocation;
    }

    /**
     * Check wether the user is connected or not
     * @return connectionActive
     */
    public boolean activeConnection() {
        return connectionActive;
    }

    /**
     * Set the state of the user's connection
     * @param state
     */
    public void setActiveConnection(boolean state) {
        connectionActive = state;
    }

    /**
     * Access the user's username
     * @return String
     */
    public String getUserName() {
        return username;
    }

    /**
     * Access the user's password
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Access the user's loginHash
     * @return double
     */
    public double getLoginHash() {
        return loginHash;
    }

    /**
     * Set the user's username to name
     * @param name
     */
    public void setUserName(String name) {
        username = name;
    }

    /**
     * Set the user's password to pass
     * @param pass
     */
    public void setPassword(String pass) {
        password = pass;
    }

    /**
     * Set the user's loginHash to hash
     * @param hash
     */
    public void setLoginHash(long hash) {
        loginHash = hash;
    }

    /**
     * Generate a loginHash for the user
     * @return long
     */
    public long generateLoginHash(String key) {
        return key.hashCode();
    }

    /**
     * Set the user's current location
     * @param: Location
     */
    public void setUserLocation(Location loc) {
        userLocation = loc;
    }

    /**
     * Get the user's current location
     * @return
     */
    public Location getUserLocation() {
        return userLocation;
    }
}
