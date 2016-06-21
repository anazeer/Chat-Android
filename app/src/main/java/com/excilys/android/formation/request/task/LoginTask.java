package com.excilys.android.formation.request.task;

import android.os.AsyncTask;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.service.ChatService;

/**
 * The task for the connection
 */
public class LoginTask extends AsyncTask<String, String, Boolean> {

    private static final String TAG = LoginTask.class.getSimpleName();

    private ChatService chatService;
    private AsyncTaskController<Boolean> listener;

    public LoginTask(ChatApplication chatApplication) {
        this.chatService = chatApplication.getChatService();
    }

    public void setLoginTaskListener(AsyncTaskController<Boolean> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    @Override
    protected Boolean doInBackground(String... args) {
        // Get the user data
        final String user = args[0];
        final String pwd = args[1];
        return chatService.login(user, pwd);
    }

    @Override
    protected void onPostExecute(Boolean s) {
        if (listener != null) {
            listener.onPostExecute(s);
        }
    }
}