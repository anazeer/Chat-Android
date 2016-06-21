package com.excilys.android.formation.service;

import android.content.Context;
import android.util.Log;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.R;
import com.excilys.android.formation.activity.MainActivity;
import com.excilys.android.formation.mapper.JsonParser;
import com.excilys.android.formation.request.RequestFactory;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Map;

/**
 * Chat service for connection, registration and listing of the messages
 */
public class ChatService {

    private static final String TAG = ChatService.class.getSimpleName();

    private Context context;
    private JsonParser jsonParser;

    public ChatService(ChatApplication chatApplication) {
        this.context = chatApplication.getApplicationContext();
        this.jsonParser = chatApplication.getJsonParser();
    }

    /**
     * Make the user registration
     * @param user the username
     * @param pwd the password
     * @return the server status reponse
     */
    public String register(String user, String pwd) {
        // Build the json
        String json = jsonParser.buildJSON(user, pwd);

        // Prepare request parameter
        String url = context.getResources().getString(R.string.register_url);
        OkHttpClient client = new OkHttpClient();
        String result = "";

        // Do the POST request
        try {
            String postResponse = RequestFactory.doPostRequest(client, url, json);
            Map<String, String> infos = jsonParser.parseConnection(postResponse);
            result = infos.get(MainActivity.JSON_STATUS);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Connect the user
     * @param user the username
     * @param pwd the password
     * @return whether the user has been successfully logged
     */
    public Boolean login(final String user, final String pwd) {
        // Save the authentication credentials
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pwd.toCharArray());
            }
        });

        // Prepare request parameter
        String url = context.getResources().getString(R.string.login_url);
        OkHttpClient client = new OkHttpClient();

        // Do the GET request
        try {
            String getResponse = RequestFactory.doGetRequest(client, url);
            Map<String, String> infos = jsonParser.parseConnection(getResponse);
            // The server response is positive
            if ("200".equals(infos.get(MainActivity.JSON_STATUS))) {
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    /**
     * Get the list of the messages from the server
     * @param args the string array containing the username, password, page limit and offset
     * @return the server response String
     */
    public String list(String... args) {

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
        String url = context.getResources().getString(R.string.list_url, limit, offset);
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

    public String send(String... args) {
        // Get the user data
        String user = args[0];
        String pwd = args[1];
        String msg = args[2];
        String mime = args[3];
        String base64 = args[4];
        mime = "image/" + mime;
        // Build the Json
        String json = jsonParser.buildJSON(user, msg, mime, base64);
        Log.i(TAG, json);

        // Prepare request parameter
        String url = context.getResources().getString(R.string.send_url);
        OkHttpClient client = new OkHttpClient();
        String result = "";

        // Do the POST request
        try {
            String postResponse = RequestFactory.doPostRequest(client, url, json);
            Map<String, String> infos = jsonParser.parseConnection(postResponse);
            result = infos.get((MainActivity.JSON_STATUS));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

}