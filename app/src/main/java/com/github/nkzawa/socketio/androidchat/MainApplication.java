package com.github.nkzawa.socketio.androidchat;

import android.app.Application;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class MainApplication extends Application {

    private static LocationTracker loc;
    private static SocketConnection sConn;

    public MainApplication() {
        loc = new LocationTracker(this);
        sConn = new SocketConnection();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public LocationTracker getLocationTracker() {
        loc = new LocationTracker(this);
        return loc;
    }

    public SocketConnection getSocketConnection() {
        return sConn;
    }

    public void displayAlert() {
        //To be implemented
    }

    public void playAlert() throws Exception {
        try {
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone tone = RingtoneManager.getRingtone(this, alarm);
            tone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
