package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.excilys.formation.exos.adapter.MessageAdapter;
import com.excilys.formation.exos.handler.RefreshHandler;
import com.excilys.formation.exos.model.Message;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.request.task.ListTask;
import com.excilys.formation.exos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Messages listing activity
 */
public class ListActivity extends Activity {

    private static final String TAG = ListActivity.class.getSimpleName();

    // Pagination parameters
    private static final String limit = "100";
    private static final String offset = "0";

    private ListView listView;
    private MessageAdapter messageAdapter;

    // User credentials
    private String user;
    private String pwd;

    // HashMap keys
    String name = "user";
    String txt = "txt";

    private Timer timer;
    private RefreshHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getExtras();
        initMessageAdapter();
        initListView();
        handler = new RefreshHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer == null) {
            initRefreshTimer();
        }
    }

    /**
     * Get the intent extras
     */
    private void getExtras() {
        user = getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getIntent().getExtras().getString(MainActivity.PWD_ID);}

    /**
     * Initialize the listView
     */
    private void initListView() {
        listView = (ListView) findViewById(R.id.list);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(messageAdapter);
    }

    /**
     * Initialize the message adapter
     */
    private void initMessageAdapter() {
        messageAdapter = new MessageAdapter(this, R.layout.right_text);
        //to scroll the listView view to bottom on data change
        messageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }

    /**
     * Initialize the timer
     */
    private void initRefreshTimer() {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                android.os.Message message = handler.obtainMessage();
                message.obj = ListActivity.this;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    /**
     * Convert the server text into a list of Message
     * @param json the text from the server
     * @return the list containing the server messages
     */
    private List<Message> getList(String json) {
        List<Message> messages = new ArrayList<>();
        List<Map<String, String>> result = JsonParser.parseMessages(json, name, txt);
        for (Map<String, String> map : result) {
            String login = map.get(name);
            String msg = map.get(txt);
            Message message = new
                    Message.Builder()
                    .login(login)
                    .message(msg)
                    .user(user.equals(login))
                    .build();
            messages.add(message);
        }
        return messages;
    }

    /**
     * Execute the list task (get the messages from the server
     * @return the server json response
     */
    private String executeTask() {
        String result = "";
        ListTask task = new ListTask();
        task.execute(user, pwd, limit, offset);
        try {
            result = task.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Refresh the messages from the server
     */
    public void refresh() {
        String json = executeTask();
        List<Message> messages = getList(json);
        messageAdapter.setChatList(messages);
    }
}