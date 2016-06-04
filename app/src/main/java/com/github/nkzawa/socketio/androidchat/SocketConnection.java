package com.github.nkzawa.socketio.androidchat;

import android.app.Application;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SocketConnection extends Application {

    private Socket mSocket;
    private boolean connected;
    {
        try {
            mSocket = IO.socket(Constants.SERVER_URL);
            connected = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            connected = false;
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public boolean isConnected() { return connected; }
}
