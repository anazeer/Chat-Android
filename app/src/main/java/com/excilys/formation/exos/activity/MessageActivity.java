package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.os.Bundle;

import com.excilys.formation.exos.R;

/**
 * Message sending activity
 */
public class MessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_layout);
    }
}