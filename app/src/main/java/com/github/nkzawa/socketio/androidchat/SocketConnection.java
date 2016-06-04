package com.github.nkzawa.socketio.androidchat;

import android.app.Application;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SocketConnection extends Application {

    private Socket sock;
    private boolean connected;

    public SocketConnection() {
        if (!connected) establishConnection();
    }

    public boolean establishConnection() {
        {
            try {
                sock = IO.socket(Constants.SERVER_URL);
                sock.connect();
                connected = true;
                System.out.println("Connection established from SocketConnection");
            } catch (URISyntaxException e) {
                System.err.println("Failed to establish connection from SocketConnection");
                e.printStackTrace();
                connected = false;
                throw new RuntimeException(e);
            }
        }
        return connected;
    }

    public Socket getSocket() {
        return sock;
    }

    public boolean isConnected() { return connected; }
}
