package com.excilys.formation.exos.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.activity.MessageActivity;
import com.excilys.formation.exos.mapper.InputStreamToString;
import com.squareup.okhttp.OkHttpClient;

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

        // Build the Json
        String json = buildJSON(user, msg);

        // Prepare request parameter
        String url = "https://training.loicortola.com/chat-rest/2.0/messages";
        OkHttpClient client = new OkHttpClient();
        String postResponse = "";

        // Do the POST request
        try {
            postResponse = RequestFactory.doPostRequest(client, url, json);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return postResponse;
    }

    private String buildJSON(String user, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate(MainActivity.JSON_UUID, UUID.randomUUID().toString());
            jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
            jsonObject.accumulate(MainActivity.JSON_MESSAGE, msg);
            //jsonObject.accumulate(MainActivity.JSON_ATTACHMENTS, "[]");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }
}