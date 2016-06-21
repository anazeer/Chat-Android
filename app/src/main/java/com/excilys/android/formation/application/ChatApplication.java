package com.excilys.android.formation.application;

import android.app.Application;

import com.excilys.android.formation.mapper.JsonParser;
import com.excilys.android.formation.service.ChatService;
import com.excilys.android.formation.util.Utils;

/**
 * Chat application singleton
 */
public class ChatApplication extends Application {

    private ChatService chatService;
    private JsonParser jsonParser;
    private Utils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        chatService = new ChatService(this);
        utils = new Utils(this.getApplicationContext());
    }

    public ChatService getChatService() {
        return chatService;
    }

    public JsonParser getJsonParser() {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }
        return jsonParser;
    }

    public Utils getUtils() {
        return utils;
    }

}