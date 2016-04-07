package com.excilys.formation.exos.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.task.MessageTask;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class SendFragment extends Fragment {

    private static final String TAG = SendFragment.class.getSimpleName();

    private EditText text;
    private Button sendButton;

    // User credentials
    private String user;
    private String pwd;

    // The message from EditText
    private String msg;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout,
                container, false);
        text = (EditText) view.findViewById(R.id.message);
        sendButton = (Button) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(sendListener);
        user = getActivity().getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getActivity().getIntent().getExtras().getString(MainActivity.PWD_ID);
        this.view = view;
        return view;
    }

    private void analyzeResult(String result) {
        Map<String, String> infos = JsonParser.parseConnection(result);
        switch (infos.get((MainActivity.JSON_STATUS))) {
            case "200":
                Toast.makeText(getActivity(),
                        "Message posté avec succès !",
                        Toast.LENGTH_SHORT).show();
                text.setText("");
                break;
            case "400":
                Toast.makeText(getActivity(),
                        "Le message a déjà été posté",
                        Toast.LENGTH_SHORT).show();
                break;
            case "401":
                Toast.makeText(getActivity(),
                        "Nom d'utilisation ou mot de passe incorrect",
                        Toast.LENGTH_SHORT).show();
            default:
                Toast.makeText(getActivity(),
                        "Erreur message",
                        Toast.LENGTH_SHORT).show();
                break;
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
                Toast.makeText(SendFragment.this.getActivity(),
                        "Le message ne doit pas être vide",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            MessageTask task = new MessageTask(view.findViewById(R.id.load));
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