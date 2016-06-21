package com.excilys.android.formation.mapper;

import android.util.Log;

import com.excilys.android.formation.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Parser for Json
 */
public class JsonParser {

    private static final String TAG = JsonParser.class.getSimpleName();

    /**
     * Convert the json String in a HashMap
     * @param json the json String
     * @return the HashMap containing the status and message
     */
    public Map<String, String> parseConnection(String json) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject reader = new JSONObject(json);
            String status = reader.getString(MainActivity.JSON_STATUS);
            String message = reader.getString(MainActivity.JSON_MESSAGE);
            result.put(MainActivity.JSON_STATUS, status);
            result.put(MainActivity.JSON_MESSAGE, message);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Convert the json String into a List of HashMap with login and message
     * @param json the json String
     * @param key1 the first key
     * @param key2 the second key
     * @return the HashMap containing the login and the message
     */
    public List<Map<String, String>> parseMessages(String json, String key1, String key2, String key3) {
        Map<String, String> element;
        List<Map<String, String>> result = new ArrayList<>();
        try {
            JSONArray reader = new JSONArray(json);
            int length = reader.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = reader.getJSONObject(i);
              //  String uuid = obj.getString(MainActivity.JSON_UUID);
                String login = obj.getString(MainActivity.JSON_LOGIN);
                String image = null;
                String message = obj.getString(MainActivity.JSON_MESSAGE);
              /*  JSONArray imagesArray = obj.getJSONArray(MainActivity.JSON_IMAGES);

                for (int j = 0; j < imagesArray.length(); j++) {
                    image = imagesArray.getString(i);
                }*/
                element = new HashMap<>();
                element.put(key1, login);
                element.put(key2, message);
                element.put(key3, image);
                result.add(element);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Build a json object with the user credentials
     * @param user the username to be putted in the json
     * @param pwd the password to be put in the json
     * @return the json result String
     */
    public String buildJSON(String user, String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
            jsonObject.accumulate(MainActivity.JSON_PWD, pwd);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Build a json object with a message and an image
     * @param user the user login
     * @param msg the message
     * @param mime the image type
     * @param base64 the image base64
     * @return the json result String
     */
    public String buildJSON(String user, String msg, String mime, String base64) {
        JSONObject jsonObject = new JSONObject();
        try {
            // Data
            jsonObject.accumulate(MainActivity.JSON_UUID, UUID.randomUUID().toString());
            jsonObject.accumulate(MainActivity.JSON_LOGIN, user);
            jsonObject.accumulate(MainActivity.JSON_MESSAGE, msg);

            // Images
            JSONObject image = new JSONObject();
            image.put(MainActivity.JSON_MIME, mime);
            image.put(MainActivity.JSON_DATA, base64);
            JSONArray attachment = new JSONArray();
            attachment.put(image);
            jsonObject.put(MainActivity.JSON_ATTACHMENTS, attachment);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "JSON : " + jsonObject.toString());
        return jsonObject.toString();
    }
}