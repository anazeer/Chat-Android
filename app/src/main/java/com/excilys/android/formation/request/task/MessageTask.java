package com.excilys.android.formation.request.task;

import android.os.AsyncTask;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.service.ChatService;

/**
 * The task for the sending messages
 */
public class MessageTask extends AsyncTask<String, String, String> {

    private static final String TAG = MessageTask.class.getSimpleName();

    private ChatService chatService;
    private AsyncTaskController<String> listener;

    public MessageTask(ChatApplication chatApplication) {
        this.chatService = chatApplication.getChatService();
    }

    public void setMessageTaskListener(AsyncTaskController<String> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    @Override
    protected String doInBackground(String... args) {
        return chatService.send(args);
    }

    @Override
    protected void onPostExecute(String s) {
        if (listener != null) {
            listener.onPostExecute(s);
        }
    }


}