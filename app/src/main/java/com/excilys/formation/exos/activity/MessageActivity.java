package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.task.MessageTask;
import com.excilys.formation.exos.R;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Message sending activity
 */
public class MessageActivity extends Activity {

    private static final String TAG = MessageActivity.class.getSimpleName();

    private EditText text;
    private Button sendButton;

    // User credentials
    private String user;
    private String pwd;

    // The message from EditText
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        text = (EditText) findViewById(R.id.message);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(sendListener);
        user = getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getIntent().getExtras().getString(MainActivity.PWD_ID);
    }

    private void analyzeResult(String result) {
        Map<String, String> infos = JsonParser.parseConnection(result);
        if ("200".equals(infos.get(MainActivity.JSON_MESSAGE))) {
            Toast.makeText(this,
                    "Message posté avec succès !",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Erreur d'envoi de message " + infos.get(MainActivity.JSON_STATUS),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Listener for the send button
     * Send the message from the EditText
     */
    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            msg = text.getText().toString();
            if (msg.trim().isEmpty()) {
                Toast.makeText(MessageActivity.this,
                        "The message shouldn't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            MessageTask task = new MessageTask(MessageActivity.this);
            sendButton.setEnabled(false);
            task.execute(user, pwd, msg);
            sendButton.setEnabled(true);
            String result = "";
            try {
                result = task.get();
                analyzeResult(result);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };
}