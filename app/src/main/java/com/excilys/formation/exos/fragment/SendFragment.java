package com.excilys.formation.exos.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.request.task.MessageTask;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Message sending fragment
 */
public class SendFragment extends Fragment {

    private static final String TAG = SendFragment.class.getSimpleName();

    private EditText text;
    private ImageButton sendButton;

    // User credentials
    private String user;
    private String pwd;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout,
                container, false);
        text = (EditText) view.findViewById(R.id.message);
        sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(sendListener);
        user = getActivity().getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getActivity().getIntent().getExtras().getString(MainActivity.PWD_ID);
        this.view = view;
        return view;
    }

    /**
     * Analyze the server response (parsing + status checking) and show the result
     * @param json the server response
     */
    private void analyzeResult(String json) {
        Map<String, String> infos = JsonParser.parseConnection(json);
        String status = infos.get((MainActivity.JSON_STATUS));
        String result = getStatusText(status);
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        text.setText("");
    }

    /**
     * Get a message depending on the server status response
     * @param status the server status response
     * @return the corresponding personalized message
     */
    private String getStatusText(String status) {
        switch (status) {
            case "200": return getResources().getString(R.string.msg_success);
            case "400": return getResources().getString(R.string.msg_exist);
            case "401": return getResources().getString(R.string.msg_illegal);
            default:    return getResources().getString(R.string.msg_failure);
        }
    }

    private String executeTask(String msg) {
        String result = "";
        MessageTask task = new MessageTask(view.findViewById(R.id.load));
        sendButton.setEnabled(false);
        task.execute(user, pwd, msg);
        sendButton.setEnabled(true);
        try {
            result = task.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    /**
     * Listener for the send button
     * Send the message from the EditText
     */
    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String msg = text.getText().toString();
            if (msg.trim().isEmpty()) {
                Toast.makeText(SendFragment.this.getActivity(),
                        getResources().getString(R.string.empty_form_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String result = executeTask(msg);
            analyzeResult(result);
        }
    };
}