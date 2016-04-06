package com.excilys.formation.exos.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.excilys.formation.exos.receiver.ParlezVousPowerReceiver;
import com.excilys.formation.exos.task.ParlezVousTask;
import com.excilys.formation.exos.R;
import com.excilys.formation.exos.task.validation.Validator;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Main activity with the connection form
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences preferences;

    private ParlezVousPowerReceiver receiver;

    // ID
    public static final String USER_ID = "user_id";
    public static final String PWD_ID = "pwd_id";

    // Errors ID
    private static final String USER_ERROR_ID = "user_error_id";
    private static final String PWD_ERROR_ID = "pwd_error_id";

    // Input form
    private EditText userText;
    private EditText pwdText;

    // Error texts
    private TextView userErrorText;
    private TextView pwdErrorText;

    // Buttons
    private Button clearButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userText = (EditText) findViewById(R.id.user);
        pwdText = (EditText) findViewById(R.id.pwd);
        userErrorText = (TextView) findViewById(R.id.userError);
        pwdErrorText = (TextView) findViewById(R.id.pwdError);
        clearButton = (Button) findViewById(R.id.clear);
        sendButton = (Button) findViewById(R.id.send);
        clearButton.setOnClickListener(clearListener);
        sendButton.setOnClickListener(sendListener);
        retrieveUser();
        setReceiver();
    }

    /**
     * Check the preferences for the user last credentials
     */
    private void retrieveUser() {
        String user = preferences.getString(USER_ID, "");
        String pwd = preferences.getString(PWD_ID, "");
        userText.setText(user);
        pwdText.setText(pwd);
    }

    /**
     * Register the action power and sms receiver
     */
    private void setReceiver() {
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        IntentFilter filter3 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiver = new ParlezVousPowerReceiver();
        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.i(TAG, "On create !");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "On destroy !");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "On pause !");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "On resume !");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "On save !");
        outState.putBoolean(USER_ERROR_ID, userErrorText.getVisibility() == View.VISIBLE);
        outState.putBoolean(PWD_ERROR_ID, pwdErrorText.getVisibility() == View.VISIBLE);
        outState.putString(USER_ID, userText.getText().toString());
        outState.putString(PWD_ID, pwdText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "On restore !");
        if (savedInstanceState.getBoolean(USER_ERROR_ID)) {
            userErrorText.setVisibility(View.VISIBLE);
        }
        if (savedInstanceState.getBoolean(PWD_ERROR_ID)) {
            pwdErrorText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Listener for the clear button
     * Clear the user inputs and the errors messages
     */
    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userText.setText("");
            pwdText.setText("");
            userErrorText.setVisibility(View.GONE);
            pwdErrorText.setVisibility(View.GONE);
        }
    };

    /**
     * Save the user credentials in the preferences
     * @param user the user name
     * @param pwd the user password
     */
    private void saveUser(String user, String pwd) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, user);
        editor.putString(PWD_ID, pwd);
        editor.apply();
    }

    /**
     *
     * @param user the user name
     * @param pwd the user password
     * @return the server response
     */
    private String executeTask(String user, String pwd) {
        ParlezVousTask task = new ParlezVousTask(MainActivity.this);
        String result = "";
        task.execute(user, pwd);
        try {
            result = task.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Show the user errors
     * @param errors the list containing the errors
     */
    private void showErrors(List<String> errors) {
        if (errors.contains(USER_ID)) {
            userErrorText.setVisibility(View.VISIBLE);
        } else {
            userErrorText.setVisibility(View.GONE);
        }
        if (errors.contains(PWD_ID)) {
            pwdErrorText.setVisibility(View.VISIBLE);
        } else {
            pwdErrorText.setVisibility(View.GONE);
        }
    }

    /**
     * Analyze the server response and do the corresponding action
     * @param result the server result
     * @param user the user name
     * @param pwd the user password
     */
    private void readServerResponse(String result, String user, String pwd) {
        // The server response is positive
        if (result.equals("true")) {
            // Save user credentials
            saveUser(user, pwd);
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
            // Access to the application
            access(user, pwd);
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Connection done, show the connected activity
     * @param user the user name
     * @param pwd the user password
     */
    private void access(String user, String pwd) {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        intent.putExtra(USER_ID, user);
        intent.putExtra(PWD_ID, pwd);
        MainActivity.this.startActivity(intent);
    }

    /**
     * Check whether the device is connected to the network
     * @return true if the network is accessible
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Listener for the send button
     * Open the drawing activity
     */
    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get the user inputs
            String user = userText.getText().toString();
            String pwd = pwdText.getText().toString();
            // Validate the inputs
            List<String> errors = Validator.validateUserCredentials(user, pwd);
            if (!errors.isEmpty()) {
                showErrors(errors);
                return;
            }
            // Everything's good
            userErrorText.setVisibility(View.GONE);
            pwdErrorText.setVisibility(View.GONE);
            // Check for the connection
            if (!isOnline()) {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                return;
            }
            sendButton.setEnabled(false);
            // Connect to the server
            String result = executeTask(user, pwd);
            sendButton.setEnabled(true);
            readServerResponse(result, user, pwd);
        }
    };
}