package com.excilys.formation.exos.request.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.request.RequestFactory;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Server registration task
 */
public class RegisterTask extends AsyncTask<String, String, String> {

    private static final String TAG = MessageTask.class.getSimpleName();

    private View view;

    public RegisterTask(View view) {
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

        // Build the json
        String json = buildJSON(user, pwd);

        // Prepare request parameter
        String url = "https://training.loicortola.com/chat-rest/2.0/register";
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

    private String buildJSON(String user, String pwd) {
        // Build the Json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
            jsonObject.accumulate(MainActivity.JSON_PWD, pwd);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }
}