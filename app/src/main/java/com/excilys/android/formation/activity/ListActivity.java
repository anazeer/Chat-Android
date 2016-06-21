package com.excilys.android.formation.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.R;
import com.excilys.android.formation.adapter.MessageAdapter;
import com.excilys.android.formation.handler.RefreshHandler;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.mapper.JsonParser;
import com.excilys.android.formation.model.Message;
import com.excilys.android.formation.request.task.ListTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Messages listing activity
 */
public class ListActivity extends Activity implements AsyncTaskController<String> {

    private static final String TAG = ListActivity.class.getSimpleName();

    // List view
    private ListView listView;
    private MessageAdapter messageAdapter;

    // User credentials
    private String user;
    private String pwd;

    // Pagination parameters
    private String limit = "150";
    private String offset = "0";

    // HashMap keys
    private String name = "user";
    private String txt = "txt";
    private String img = "img";

    // Timer
    private Timer timer;
    private RefreshHandler handler;

    // Task
    private ListTask listTask;

    // View
    private ProgressBar progressBar;

    // Json parser
    private JsonParser jsonParser;

    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        askPermission();
        getExtras();
        initMessageAdapter();
        initViews();
        handler = new RefreshHandler();
        jsonParser = ((ChatApplication) getApplication()).getJsonParser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer == null) {
            initRefreshTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        cancelTasks();
    }

    private void askPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Get the intent extras
     */
    private void getExtras() {
        user = getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getIntent().getExtras().getString(MainActivity.PWD_ID);}

    /**
     * Initialize the views
     */
    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.load);
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
        timer.schedule(task, 0, 10000);
    }

    /**
     * Cancel all current tasks
     */
    private void cancelTasks() {
        if (listTask != null) {
            listTask.cancel(true);
        }
    }

    /**
     * Get all users messages from the server
     */
    public void list() {
        if (listTask != null && listTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            listTask.cancel(true);
        }
        listTask = new ListTask((ChatApplication) getApplication());
        listTask.setListTaskListener(this);
        listTask.execute(user, pwd, limit, offset);
    }

    /**
     * Convert the server text into a list of Message
     * @param json the text from the server
     * @return the list containing the server messages
     */
    private List<Message> getList(String json) {
        List<Message> messages = new ArrayList<>();
        List<Map<String, String>> result = jsonParser.parseMessages(json, name, txt, img);
        for (Map<String, String> map : result) {
            String login = map.get(name);
            String msg = map.get(txt);
            String image = map.get(img);
            Message message = new
                    Message.Builder()
                    .login(login)
                    .message(msg)
                    .user(user.equals(login))
                    .image(image)
                    .build();
            messages.add(message);
        }
        return messages;
    }

    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(String result) {
        progressBar.setVisibility(View.INVISIBLE);
        List<Message> messages = getList(result);
        messageAdapter.setChatList(messages);
    }
}