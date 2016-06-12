package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends FragmentActivity {

    private Socket mSocket;
    private ApplicationManager app;
    private RemoteDevice remoteDevice;
    protected static boolean mLoginStatus;
    protected String mUsername, mPassword;
    protected EditText mUsernameView, mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (ApplicationManager) getApplication();
        mSocket = app.getSocketConnection().getSocket();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mPasswordView = (EditText) findViewById(R.id.password_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return attemptLogin();
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mSwitchToNewFragment = attemptLogin();
                if (MainActivity.mSwitchToNewFragment) {
                    System.out.println("mSwitchToNewFragment set to True");
                    //FIXME: Implement this alert elsewhere
                    try { app.playAlert();}
                    catch(Exception e) {e.printStackTrace();}
                } else {
                    System.out.println("mSwitchToNewFragment set to false");
                    attemptLogin();
                }
            }
        });

        // Switch back to the Main Activity
        // Check the SwitchToNewFragment there and respond
        if (MainActivity.mSwitchToNewFragment) {
            Intent intent = getParentActivityIntent();
            startActivity(intent);
        }

        mSocket.on("login", onLogin);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mUsernameView != null && mUsername != null)     mUsernameView.setText(mUsername);
        if (mPasswordView != null && mPassword != null)     mPasswordView.setText(mPassword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return false;
        }
        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");
            mPasswordView.requestFocus();
            return false;
        }

        remoteDevice = new RemoteDevice(username, password);

        // perform the remoteDevice login attempt.
        mSocket.emit("add remoteDevice", username);
        return true;
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", remoteDevice.getUserName());
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}



