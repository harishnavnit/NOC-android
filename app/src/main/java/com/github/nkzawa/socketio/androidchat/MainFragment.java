package com.github.nkzawa.socketio.androidchat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A chat fragment containing messages view and input form.
 */
public class MainFragment extends Fragment {

    // Private data
    protected static Socket mSocket;
    protected static WebView mWebView;
    protected static String mUsername;
    protected static TableLayout mProximityTable;
    protected static boolean mLocationButtonPressed;
    protected static int mLocationButtonPressedCount;

    // UI widgets
    protected static Button mLocationButton;
    protected static TextView mLocationDisplay, mUserNameDisplay;

    // Static data
    protected static ApplicationManager mApp;
    protected static final int REQUEST_LOGIN = 0;

    public MainFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mApp = (ApplicationManager) getActivity().getApplication();
        mSocket = mApp.getSocketConnection().getSocket();
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("alert received", onAlertReceived);
            mSocket.on("user joined", onUserJoined);
            mSocket.on("user left", onUserLeft);
        mApp.getSocketConnection().establishConnection();
        startSignIn();
        attemptSend();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("alert received", onAlertReceived);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the UI widgets
        mLocationButtonPressedCount = 1;
        mLocationButton = (Button) view.findViewById(R.id.locationButton);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mLocationButtonPressedCount;
                mLocationButtonPressed = (mLocationButtonPressedCount % 2 == 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        } else {
            System.err.println("Activity failed unexpectedly");
        }

        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        addLog("Welcome to proximity notifier");
        addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addLog(String message) {
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void attemptSend() {
        System.out.println("MainFragment::attemptSend()");
        if (!mSocket.connected()) return;

        System.out.println("attemptSend() -- sending");
        // perform the sending message attempt.
        mSocket.emit("Latitude", LocationTracker.mCurrentLocation.getLatitude());
        mSocket.emit("Longitude",LocationTracker.mCurrentLocation.getLongitude());
    }

    private void startSignIn() {
        mUsername = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Display and/or sound an alert
     */
    private Emitter.Listener onAlertReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    public void formProximityTableHeaders() {
        // Form the table header
        TableRow header = new TableRow(getContext());
        header.setBackgroundColor(Color.DKGRAY);
        header.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Prepare columns to be added to the table header
        TextView firstColumn, secondColumn, thirdColumn;
        firstColumn = new TextView(getContext()); firstColumn.setText("Username/Device Id"); firstColumn.setPadding(1, 1, 1, 1);
        secondColumn = new TextView(getContext()); secondColumn.setText("Distance"); secondColumn.setPadding(1, 1, 1, 1);
        thirdColumn = new TextView(getContext()); thirdColumn.setText("Severity"); thirdColumn.setPadding(1, 1, 1, 1);

        // Insert columns into the table header
        header.addView(firstColumn); header.addView(secondColumn); header.addView(thirdColumn);

        // Add the header row to the table layout
        mProximityTable.addView(header, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

