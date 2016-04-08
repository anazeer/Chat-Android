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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.excilys.formation.exos.receiver.PowerReceiver;
import com.excilys.formation.exos.request.task.ConnectionTask;
import com.excilys.formation.exos.R;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.request.task.RegisterTask;
import com.excilys.formation.exos.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Main activity with the connection form
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences preferences;

    private PowerReceiver receiver;

    private Menu menu;

    // Preferences ID
    public static final String USER_ID = "user_id";
    public static final String PWD_ID = "pwd_id";

    // Errors ID
    public static final String USER_ERROR_ID = "user_error_id";
    public static final String PWD_ERROR_ID = "pwd_error_id";

    // JSON ID
    public static final String JSON_STATUS = "status";
    public static final String JSON_MESSAGE = "message";
    public static final String JSON_LOGIN = "login";
    public static final String JSON_PWD = "password";
    public static final String JSON_UUID = "uuid";
    public static final String JSON_ATTACHMENTS = "attachments";
    public static final String JSON_MIME = "mimeType";
    public static final String JSON_DATA = "data";

// Input form
    private EditText userText;
    private EditText pwdText;

    // Error texts
    private TextView userErrorText;
    private TextView pwdErrorText;

    // Buttons
    private Button clearButton;
    private Button sendButton;
    private Button registerButton;

    // User inputs
    private String user;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initButtons();
        initTextViews();
        retrieveUser();
        setReceiver();
    }

    /**
     * Initialize the buttons
     */
    private void initButtons() {
        clearButton = (Button) findViewById(R.id.clear);
        sendButton = (Button) findViewById(R.id.send);
        registerButton = (Button) findViewById(R.id.register);
        clearButton.setOnClickListener(clearListener);
        sendButton.setOnClickListener(sendListener);
        registerButton.setOnClickListener(registerListener);
    }

    /**
     * Initialize the text views
     */
    private void initTextViews() {
        userText = (EditText) findViewById(R.id.user);
        pwdText = (EditText) findViewById(R.id.pwd);
        userErrorText = (TextView) findViewById(R.id.userError);
        pwdErrorText = (TextView) findViewById(R.id.pwdError);
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
     * RegisterTask the action power and sms receiver
     */
    private void setReceiver() {
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        IntentFilter filter3 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiver = new PowerReceiver();
        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.register_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register:
                register();
                return true;
        }
        return true;
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
     * Listener for the register button
     * Make the user registration
     */
    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            register();
        }
    };

    /**
     * Save the user credentials in the preferences
     */
    private void saveUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, user);
        editor.putString(PWD_ID, pwd);
        editor.apply();
    }

    /**
     *
     * @return the server response
     */
    private String executeTask() {
        ConnectionTask task = new ConnectionTask(MainActivity.this.findViewById(R.id.load));
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
     */
    private void readServerResponse(String result) {
        Map<String, String> infos = JsonParser.parseConnection(result);
        // The server response is positive
        if ("200".equals(infos.get(JSON_STATUS))) {
            // Save user credentials
            saveUser();
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
            // Access to the application
            access(user, pwd);
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Connection done, show the connected activity
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
     * Check the user inputs and update the error text
     * @return true if the inputs have been validated, false otherwise
     */
    private boolean checkInputs() {
        // Get the user inputs
        user = userText.getText().toString();
        pwd = pwdText.getText().toString();
        // Validate the inputs
        List<String> errors = Validator.validateUserCredentials(user, pwd);
        if (!errors.isEmpty()) {
            showErrors(errors);
            return false;
        }
        // Everything's good
        userErrorText.setVisibility(View.GONE);
        pwdErrorText.setVisibility(View.GONE);
        return true;
    }

    private String executeRegisterTask() {
        String result = "";
        RegisterTask task = new RegisterTask(MainActivity.this.findViewById(R.id.load));
        task.execute(user, pwd);
        try {
            result = task.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Get a message depending on the server status response
     * @param status the server status response
     * @return the corresponding personalized message
     */
    private String getStatusText(String status) {
        switch (status) {
            case "200": return getResources().getString(R.string.register_success);
            case "400": return getResources().getString(R.string.register_exist);
            default:    return getResources().getString(R.string.register_fail);
        }
    }

    /**
     * Make the user registration
     */
    private void register() {
        if (!checkInputs()) {
            return;
        }
        String json = executeRegisterTask();
        Map<String, String> infos = JsonParser.parseConnection(json);
        String result = getStatusText(infos.get(MainActivity.JSON_STATUS));
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener for the send button
     * Open the drawing activity
     */
    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkInputs()) {
                return;
            }
            // Everything's good
            userErrorText.setVisibility(View.GONE);
            pwdErrorText.setVisibility(View.GONE);
            // Check for the connection
            if (!isOnline()) {
                Toast.makeText(MainActivity.this,
                        MainActivity.this.getResources().getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendButton.setEnabled(false);
            // Connect to the server
            String result = executeTask();
            sendButton.setEnabled(true);
            readServerResponse(result);
        }
    };
}