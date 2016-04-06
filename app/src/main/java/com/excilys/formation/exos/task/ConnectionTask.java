package com.excilys.formation.exos.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.mapper.InputStreamToString;
import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;

/**
 * The task for the connection
 */
public class ConnectionTask extends AsyncTask<String, String, String> {

    private static final String TAG = ConnectionTask.class.getSimpleName();

    private MainActivity main;
    private View view;

    public ConnectionTask(MainActivity main) {
        view = main.findViewById(R.id.load);
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
        String result = "";

        // Get the user datas
        final String user = args[0];
        final String pwd = args[1];

        // Save the authentication credentials
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pwd.toCharArray());
            }
        });

        HttpURLConnection conn = null;
        try {
            // Open the url connection
            String urlText = "https://training.loicortola.com/chat-rest/2.0/connect";
            URL url = new URL(urlText);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Start the query
            conn.connect();
            InputStream is = new BufferedInputStream(conn.getInputStream());

            // Convert the result
            result = InputStreamToString.convert(is);
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}