package com.excilys.formation.exos.request.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.request.RequestFactory;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * The task for the connection
 */
public class ConnectionTask extends AsyncTask<String, String, String> {

    private static final String TAG = ConnectionTask.class.getSimpleName();

    private View view;

    public ConnectionTask(View view) {
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        view.setVisibility(View.GONE);
    }

    @Override
    protected String doInBackground(String... args) {

        // Get the user data
        final String user = args[0];
        final String pwd = args[1];

        // Save the authentication credentials
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pwd.toCharArray());
            }
        });

        // Prepare request parameter
        String url = "https://training.loicortola.com/chat-rest/2.0/connect";
        OkHttpClient client = new OkHttpClient();
        String getResponse = "";

        // Do the GET request
        try {
            getResponse = RequestFactory.doGetRequest(client, url);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return getResponse;
    }
}