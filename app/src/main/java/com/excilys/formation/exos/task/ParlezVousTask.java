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
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The task for the connection
 */
public class ParlezVousTask extends AsyncTask<String, String, String> {

    private static final String TAG = ParlezVousTask.class.getSimpleName();

    private MainActivity main;
    private View view;

    public ParlezVousTask(MainActivity main) {
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
        String user = args[0];
        String pwd = args[1];

        HttpURLConnection conn = null;
        try {
            // Open the url connection
            String urlText = String.format
                    ("http://formation-android-esaip.herokuapp.com/connect/%s/%s", user, pwd);
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