package com.excilys.formation.exos.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.excilys.formation.exos.R;
import com.excilys.formation.exos.activity.MainActivity;
import com.excilys.formation.exos.mapper.JsonParser;
import com.excilys.formation.exos.request.task.MessageTask;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Message sending fragment
 */
public class SendFragment extends Fragment {

    private static final String TAG = SendFragment.class.getSimpleName();
    private static final int GALLERY_REQUEST_CODE = 1;

    private EditText text;
    private ImageButton sendButton;
    String imgDecodableString;
    String base64 = "";
    String mime = "";

    private Button image;

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
        image = (Button) view.findViewById(R.id.browse);
        image.setOnClickListener(imageListener);
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
        Log.d(TAG, json);
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
        task.execute(user, pwd, msg, mime, base64);
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

    private View.OnClickListener imageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
            pickImageIntent.setType("image/*");
            startActivityForResult(pickImageIntent, GALLERY_REQUEST_CODE);
        }
    };

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
                imgDecodableString = cursor.getString(columnIndex);
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

                // get the mime
                ContentResolver cr = getActivity().getContentResolver();
                MimeTypeMap m = MimeTypeMap.getSingleton();
                mime = m.getExtensionFromMimeType(cr.getType(selectedImage));


            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}