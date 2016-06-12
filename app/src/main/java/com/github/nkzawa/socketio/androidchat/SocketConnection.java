package com.github.nkzawa.socketio.androidchat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by harish on 05/06/16.
 */
public class SocketConnection {
    protected static Socket mSocket;
    protected static boolean mConnected;

    SocketConnection() {
        if (! mConnected ) establishConnection();
    }

    public boolean isConnected() {
        return mConnected;
    }

    public boolean establishConnection() {
        {
            try {
                mSocket = IO.socket(Constants.SERVER_URL);
                mSocket.connect();
                mConnected = true;
                System.out.println("Connection established from SocketConnection");
            } catch (URISyntaxException e) {
                System.err.println("Failed to establish connection from ApplicationManager");
                e.printStackTrace();
                mConnected = false;
                throw new RuntimeException(e);
            }
        }
        return mConnected;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public boolean closeConnection() {
        if (mConnected) {
            mSocket.disconnect();
            mConnected = false;
        }
        return mConnected;
    }

    public void sendCurrentLocation() {
        double lat, lng;
        if (LocationTracker.mCurrentLocation != null) {
            lat = LocationTracker.mCurrentLocation.getLatitude();
            lng = LocationTracker.mCurrentLocation.getLongitude();
        } else {
            System.err.println("Unable to fetch current location");
            lat = 0.0;  lng = 0.0;
        }
        System.out.println("Emitting data to source");
        mSocket.emit("Latitude", lat);
        mSocket.emit("Longitude", lng);
    }

    public void receiveNewLocation() {

    }
}
