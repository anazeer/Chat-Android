package com.excilys.formation.exos.request.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.request.RequestFactory;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * The task for the sending messages
 */
public class MessageTask extends AsyncTask<String, String, String> {

    private static final String TAG = MessageTask.class.getSimpleName();

    private View view;

    public MessageTask(View view) {
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
            // Data
            jsonObject.accumulate(MainActivity.JSON_UUID, UUID.randomUUID().toString());
            jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
            jsonObject.accumulate(MainActivity.JSON_MESSAGE, msg);

            // Images
            JSONObject image = new JSONObject();
            image.put(MainActivity.JSON_MIME, "");
            image.put(MainActivity.JSON_DATA, "");
            JSONArray attachment = new JSONArray();
            attachment.put(image);
            jsonObject.put(MainActivity.JSON_ATTACHMENTS, attachment);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }
}