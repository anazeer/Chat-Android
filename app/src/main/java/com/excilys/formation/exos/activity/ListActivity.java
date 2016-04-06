package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.task.ListTask;
import com.excilys.formation.exos.R;

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
        refreshButton.performClick(); // refresh the list the first time
    }

    /**
     * Convert the server text into a ListAdapter and set it to the ListView
     * @param s the text from the server
     */
    private void updateMessages(String s) {
        List<Map<String, String>> result = JsonParser.parseMessages(s, name, txt);
        ListAdapter adapter = new SimpleAdapter(this,
                result,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {name, txt},
                new int[] {android.R.id.text1, android.R.id.text2});
        list.setAdapter(adapter);
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
            updateMessages(result);
        }
    };
}