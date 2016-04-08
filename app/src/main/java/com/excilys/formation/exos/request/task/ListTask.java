package com.excilys.formation.exos.request.task;

import android.os.AsyncTask;
import android.util.Log;

import com.excilys.formation.exos.request.RequestFactory;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * The task for the messages listing
 */
public class ListTask extends AsyncTask<String, String, String> {

    private static final String TAG = ListTask.class.getSimpleName();

    public ListTask() {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... args) {

        // Get the user data
        final String user = args[0];
        final String pwd = args[1];
        final String limit = args[2];
        final String offset = args[3];

        // Save the authentication credentials
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pwd.toCharArray());
            }
        });

        // Prepare request parameter
        String url = String.format("https://training.loicortola.com/chat-rest/2.0/" +
                "messages?&limit=%s&offset=%s", limit, offset);
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