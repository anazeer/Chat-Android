package com.excilys.formation.exos.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.activity.MessageActivity;
import com.excilys.formation.exos.mapper.InputStreamToString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * The task for the sending messages
 */
public class MessageTask extends AsyncTask<String, String, String> {

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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        view.setVisibility(View.GONE);
    }

    @Override
    protected String doInBackground(String... args) {
        // Get the user data
        String user = args[0];
        String pwd = args[1];
        String msg = args[2];

        String result = "";

        HttpURLConnection conn = null;
        try {
            // Build the url connection
            String urlText = "https://training.loicortola.com/chat-rest/2.0/messages";
            URL url = new URL(urlText);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Build the Json
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate(MainActivity.JSON_UUID, UUID.randomUUID());
                jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
                jsonObject.accumulate(MainActivity.JSON_MESSAGE, msg);
                //jsonObject.accumulate(MainActivity.JSON_ATTACHMENTS, "[]");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            String json = jsonObject.toString();
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(json);
            out.close();

            // Start the query
            conn.connect();
            InputStream is = new BufferedInputStream(conn.getInputStream());

            // Convert the result
            result = InputStreamToString.convert(is);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}