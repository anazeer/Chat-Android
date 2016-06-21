package com.excilys.android.formation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.R;
import com.excilys.android.formation.validation.Validator;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.request.task.LoginTask;
import com.excilys.android.formation.request.task.RegisterTask;
import com.excilys.android.formation.util.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskController<Boolean> {

    // Tag for the logger
    private final String TAG = MainActivity.class.getSimpleName();

    // Credentials ID
    public static final String USER_ID = "user_id";
    public static final String PWD_ID = "pwd_id";

    // Errors ID
    public static final String USER_ERROR_ID = "user_error_id";
    public static final String PWD_ERROR_ID = "pwd_error_id";

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

    // Preferences
    private SharedPreferences preferences;

    // Views
    private ProgressBar progressBar;

    // Tasks
    private RegisterTask registerTask;
    private LoginTask loginTask;

    // Application singleton
    private ChatApplication chatApplication;
    private Utils utils;

    // JSON ID
    public static final String JSON_STATUS = "status";
    public static final String JSON_MESSAGE = "message";
    public static final String JSON_LOGIN = "login";
    public static final String JSON_PWD = "password";
    public static final String JSON_UUID = "uuid";
    public static final String JSON_ATTACHMENTS = "attachments";
    public static final String JSON_MIME = "mimeType";
    public static final String JSON_DATA = "data";
    public static final String JSON_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        chatApplication = (ChatApplication) getApplication();
        utils = chatApplication.getUtils();
        initButtons();
        initViews();
        retrieveUser();
    }

    /**
     * Initialize the buttons
     */
    private void initButtons() {
        clearButton = (Button) findViewById(R.id.clear);
        sendButton = (Button) findViewById(R.id.send);
        registerButton = (Button) findViewById(R.id.register);
        clearButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    /**
     * Initialize the views
     */
    private void initViews() {
        userText = (EditText) findViewById(R.id.user);
        pwdText = (EditText) findViewById(R.id.pwd);
        userErrorText = (TextView) findViewById(R.id.userError);
        pwdErrorText = (TextView) findViewById(R.id.pwdError);
        progressBar = (ProgressBar) findViewById(R.id.load);
    }

    /**
     * Cancel all current tasks
     */
    private void cancelTasks() {
        if (registerTask != null) {
            registerTask.cancel(true);

        }
        if (loginTask != null) {
            loginTask.cancel(true);
        }
    }

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
     * Check the preferences for the user last credentials
     */
    private void retrieveUser() {
        String user = preferences.getString(USER_ID, "");
        String pwd = preferences.getString(PWD_ID, "");
        userText.setText(user);
        pwdText.setText(pwd);
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
     * Action for the clear button
     * Clear the user inputs and the errors messages
     */
    private void clear() {
        userText.setText("");
        pwdText.setText("");
        userErrorText.setVisibility(View.GONE);
        pwdErrorText.setVisibility(View.GONE);
    }

    /**
     * Start the logged activity (dashboard)
     */
    private void connection() {
        Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
        intent.putExtra(USER_ID, user);
        intent.putExtra(PWD_ID, pwd);
        startActivity(intent);
    }

    /**
     * Check the user credentials for logging
     */
    private void login() {
        if (!checkInputs()) {
            return;
        }
        if (!utils.isOnline()) {
            Toast.makeText(this, getResources().getText(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
        if (loginTask != null && loginTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            loginTask.cancel(true);
        }
        loginTask = new LoginTask(chatApplication);
        loginTask.setLoginTaskListener(this);
        loginTask.execute(user, pwd);
    }

    /**
     * Make the user registration
     */
    private void register() {
        if (!checkInputs()) {
            return;
        }
        if (!utils.isOnline()) {
            Toast.makeText(this, getResources().getText(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
        if (registerTask != null && registerTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            loginTask.cancel(true);
        }
        registerTask = new RegisterTask(chatApplication);
        registerTask.setRegisterTaskListener(new AsyncTaskController<String>() {
            @Override
            public void onPreExecute() {
                beforeTask();
            }

            @Override
            public void onPostExecute(String result) {
                String text = utils.getRegistrationStatusText(result);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                afterTask();
            }
        });
        registerTask.execute(user, pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register :
                register();
                break;
            case R.id.send :
                login();
                break;
            case R.id.clear :
                clear();
                break;
        }
    }

    @Override
    public void onPreExecute() {
        beforeTask();
    }

    @Override
    public void onPostExecute(Boolean result) {
        if (result) {
            // Save user credentials
            saveUser();
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.conn_success), Toast.LENGTH_SHORT).show();
            // Access to the application
            connection();
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
        }
        afterTask();
    }

    /**
     * Disable the buttons and show the progress bar
     */
    private void beforeTask() {
        progressBar.setVisibility(View.VISIBLE);
        clearButton.setEnabled(false);
        sendButton.setEnabled(false);
        registerButton.setEnabled(false);
    }

    /**
     * Enable the buttons and discard the progress bar
     */
    private void afterTask() {
        progressBar.setVisibility(View.INVISIBLE);
        clearButton.setEnabled(true);
        sendButton.setEnabled(true);
        registerButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTasks();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(USER_ERROR_ID)) {
            userErrorText.setVisibility(View.VISIBLE);
        }
        if (savedInstanceState.getBoolean(PWD_ERROR_ID)) {
            pwdErrorText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(USER_ERROR_ID, userErrorText.getVisibility() == View.VISIBLE);
        outState.putBoolean(PWD_ERROR_ID, pwdErrorText.getVisibility() == View.VISIBLE);
        outState.putString(USER_ID, userText.getText().toString());
        outState.putString(PWD_ID, pwdText.getText().toString());
    }
}