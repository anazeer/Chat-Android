package com.excilys.formation.exos.mapper;

import android.util.Log;

import com.excilys.formation.exos.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static Map<String, String> parseConnection(String json) {
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
    public static List<Map<String, String>> parseMessages(String json, String key1, String key2, String key3) {
        Map<String, String> element;
        List<Map<String, String>> result = new ArrayList<>();
        try {
            JSONArray reader = new JSONArray(json);
            int length = reader.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = reader.getJSONObject(i);
                String login = obj.getString(MainActivity.JSON_LOGIN);
                String message = obj.getString(MainActivity.JSON_MESSAGE);
                //JSONObject imageObject = obj.getJSONObject(MainActivity.JSON_IMAGES);
                //String image = imageObject.getString();
                //String uuid = obj.getString(MainActivity.JSON_UUID);
                //String status = obj.getString(MainActivity.JSON_STATUS);
                element = new HashMap<>();
                element.put(key1, login);
                element.put(key2, message);
                //element.put(key3, image);
                result.add(element);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }
}