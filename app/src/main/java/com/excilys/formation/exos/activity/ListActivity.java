package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.excilys.formation.exos.adapter.MessageArrayAdapter;
import com.excilys.formation.exos.model.Message;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.task.ListTask;
import com.excilys.formation.exos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Messages listing activity
 */
public class ListActivity extends Activity {

    private static final String TAG = ListActivity.class.getSimpleName();

    private ListView list;
    private Button refreshButton;
    private MessageArrayAdapter messageArrayAdapter;

    // User credentials
    private String user;
    private String pwd;

    // HashMap keys
    String name = "user";
    String txt = "txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        list = (ListView) findViewById(R.id.list);
        refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(refreshListener);
        user = getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getIntent().getExtras().getString(MainActivity.PWD_ID);

        messageArrayAdapter = new MessageArrayAdapter(this, R.layout.right_text);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(messageArrayAdapter);

        //to scroll the list view to bottom on data change
        messageArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                list.setSelection(messageArrayAdapter.getCount() - 1);
            }
        });

        // refresh the list the first time
        refreshButton.performClick();
    }

    /**
     * Convert the server text into a list of Message and set it to the ListView
     * @param s the text from the server
     */
    private void updateList(String s) {
        List<Message> messages = new ArrayList<>();
        List<Map<String, String>> result = JsonParser.parseMessages(s, name, txt);
        for( Map<String, String> map : result) {
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
        messageArrayAdapter.setChatList(messages);
    }

    /**
     * Listener for the refresh button
     */
    private View.OnClickListener refreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String result = "";
            ListTask task = new ListTask(ListActivity.this);
            refreshButton.setEnabled(false);
            task.execute(user, pwd);
            try {
                result = task.get();
                Toast.makeText(ListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, e.getMessage());
            }
            refreshButton.setEnabled(true);
            updateList(result);
        }
    };
}