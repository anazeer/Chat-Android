package com.excilys.android.formation.request;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Requests factory
 */
public class RequestFactory {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Make a POST request
     * @param client the client
     * @param url the url
     * @param json the request body
     * @return the server response
     * @throws IOException
     */
    public static String doPostRequest(OkHttpClient client, String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Make a GET request
     * @param client the client
     * @param url the url
     * @return the server response
     * @throws IOException
     */
    public static String doGetRequest(OkHttpClient client, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}