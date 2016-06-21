package com.excilys.android.formation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.excilys.android.formation.application.ChatApplication;
import com.excilys.android.formation.R;
import com.excilys.android.formation.activity.MainActivity;
import com.excilys.android.formation.listener.AsyncTaskController;
import com.excilys.android.formation.request.task.MessageTask;
import com.excilys.android.formation.util.Utils;

import java.io.ByteArrayOutputStream;

/**
 * Message sending fragment
 */
public class SendFragment extends Fragment implements View.OnClickListener, AsyncTaskController<String> {

    // Tag for the logger
    private static final String TAG = SendFragment.class.getSimpleName();

    // Gallery request
    private static final int GALLERY_REQUEST_CODE = 1;

    // Views
    private View view;
    private EditText text;
    private ImageButton sendButton;
    private Button browseButton;
    private ProgressBar progressBar;

    // User credentials
    private String user;
    private String pwd;

    // User text
    private String msg;

    // Image info
    private String base64 = "";
    private String mime = "";

    // Task
    private MessageTask messageTask;

    // Utils singleton
    private Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout,
                container, false);
        getExtras();
        utils = ((ChatApplication) getActivity().getApplication()).getUtils();
        this.view = view;
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelTasks();
    }

    /**
     * Get the extras from the parent intent
     */
    private void getExtras() {
        user = getActivity().getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getActivity().getIntent().getExtras().getString(MainActivity.PWD_ID);
    }

    /**
     * Initialize the views
     */
    private void initViews() {
        text = (EditText) view.findViewById(R.id.message);
        text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendButton.performClick();
                    return true;
                }
                return false;
            }
        });
        sendButton = (ImageButton) view.findViewById(R.id.send_button);
        browseButton = (Button) view.findViewById(R.id.browse);
        progressBar = (ProgressBar) view.findViewById(R.id.load);
    }

    /**
     * Initialize buttons
     */
    private void initListeners() {
        sendButton.setOnClickListener(this);
        browseButton.setOnClickListener(this);
    }

    /**
     * Cancel all current tasks
     */
    private void cancelTasks() {
        if (messageTask != null) {
            messageTask.cancel(true);
        }
    }

    /**
     * Send the message to the server
     */
    private void send() {
        msg = text.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(SendFragment.this.getActivity(),
                    getResources().getString(R.string.empty_form_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (messageTask != null && messageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            messageTask.cancel(true);
        }
        messageTask = new MessageTask((ChatApplication) getActivity().getApplication());
        messageTask.setMessageTaskListener(this);
        messageTask.execute(user, pwd, msg, mime, base64);
    }

    /**
     * Select an image from the device
     */
    private void browse() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) view.findViewById(R.id.image);
                // Set the Image in ImageView after decoding the String
                Bitmap bitmap = BitmapFactory
                        .decodeFile(imgDecodableString);
                imgView.setImageBitmap(bitmap);

                // Convert to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArrayImage = baos.toByteArray();
                base64 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                // Get the mime
                ContentResolver cr = getActivity().getContentResolver();
                MimeTypeMap m = MimeTypeMap.getSingleton();
                mime = m.getExtensionFromMimeType(cr.getType(selectedImage));

            } else {
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.image_empty),
                        Toast.LENGTH_LONG)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(R.string.image_error),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
    }

    @Override
    public void onPostExecute(String result) {
        progressBar.setVisibility(View.INVISIBLE);
        sendButton.setEnabled(true);
        String status = utils.getMessageStatusText(result);
        Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
        text.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button : send(); break;
            case R.id.browse : browse(); break;
        }
    }
}