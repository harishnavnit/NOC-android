package com.github.nkzawa.socketio.androidchat;

import android.app.Application;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class ApplicationManager extends Application {

    private static SocketConnection mSocketConnection;

    public ApplicationManager() {
        mSocketConnection = new SocketConnection();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public SocketConnection getSocketConnection() {
        return mSocketConnection;
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
