package com.github.nkzawa.socketio.androidchat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by harish on 05/06/16.
 */
public class SocketConnection {
    private static Socket sock;
    private static boolean connected;

    SocketConnection() {
        if (! connected ) establishConnection();
    }

    public boolean isConnected() {
        return connected;
    }
    public boolean establishConnection() {
        {
            try {
                sock = IO.socket(Constants.SERVER_URL);
                sock.connect();
                connected = true;
                System.out.println("Connection established from SocketConnection");
            } catch (URISyntaxException e) {
                System.err.println("Failed to establish connection from ApplicationManager");
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

    public boolean closeConnection() {
        if (connected) {
            sock.disconnect();
            connected = false;
        }
        return connected;
    }
}
