package com.excilys.android.formation.request.task;

import android.os.AsyncTask;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.service.ChatService;

/**
 * Server registration task
 */
public class RegisterTask extends AsyncTask<String, String, String> {

    private static final String TAG = MessageTask.class.getSimpleName();

    private ChatService chatService;
    private AsyncTaskController<String> listener;

    public RegisterTask(ChatApplication chatApplication) {
        this.chatService = chatApplication.getChatService();
    }

    public void setRegisterTaskListener(AsyncTaskController<String> listener) {
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
        // Get the user data
        final String user = args[0];
        final String pwd = args[1];
        return chatService.register(user, pwd);
    }

    @Override
    protected void onPostExecute(String s) {
        if (listener != null) {
            listener.onPostExecute(s);
        }
    }
}