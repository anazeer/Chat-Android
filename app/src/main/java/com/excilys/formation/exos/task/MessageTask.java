package com.excilys.formation.exos.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MessageActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The task for the sending messages
 */
public class MessageTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = MessageTask.class.getSimpleName();

    private View view;

    public MessageTask(MessageActivity main) {
        view = main.findViewById(R.id.load);
    }

    @Override
    protected void onPreExecute() {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        view.setVisibility(View.GONE);
    }

    @Override
    protected Void doInBackground(String... args) {
        // Get the user data
        String user = args[0];
        String pwd = args[1];
        String msg = args[2];

        HttpURLConnection conn = null;
        try {
            // Open the url connection
            String urlText = String.format
                    ("http://formation-android-esaip.herokuapp.com/message/%s/%s/%s",
                            user, pwd, msg);
            URL url = new URL(urlText);
            conn = (HttpURLConnection) url.openConnection();
            // Start the query
            conn.connect();
            conn.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}