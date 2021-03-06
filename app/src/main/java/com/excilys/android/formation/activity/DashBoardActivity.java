package com.excilys.android.formation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.excilys.android.formation.R;

/**
 * Menu activity once connected
 */
public class DashBoardActivity extends Activity implements View.OnClickListener {

    private Button drawButton;
    private Button listButton;
    private Button sendButton;

    // User credentials
    private String user;
    private String pwd;

    private Menu menu;
    private AlertDialog.Builder confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        initButtons();
        getExtras();
        initConfirmDialog();
    }

    /**
     * Initialize the buttons and set the listeners
     */
    private void initButtons() {
        listButton = (Button) findViewById(R.id.list_button);
        sendButton = (Button) findViewById(R.id.send_button);
        listButton.setOnClickListener(this);
    }

    /**
     * Get the extras from the intent
     */
    private void getExtras() {
        user = getIntent().getExtras().getString(MainActivity.USER_ID);
        pwd = getIntent().getExtras().getString(MainActivity.PWD_ID);
    }

    /**
     * Initialize the confirmation alert dialog
     */
    private void initConfirmDialog() {
        confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle(getResources().getString(R.string.confirm_title));
        confirmDialog.setMessage(getResources().getString(R.string.confirm)).
                setPositiveButton(getResources().getString(R.string.yes), confirmDialogListener).
                setNegativeButton(getResources().getString(R.string.no), confirmDialogListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disconnect:
                confirmDialog.show();
                return true;
            case R.id.cancel:
                return true;
        }
        return true;
    }

    /**
     * The replay dialog buttons listener
     */
    DialogInterface.OnClickListener confirmDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent main = new Intent(DashBoardActivity.this, MainActivity.class);
                    startActivity(main);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    /**
     * Action for the list button
     * Open the listing activity
     */
    private void list() {
        Intent intent = new Intent(DashBoardActivity.this, ListActivity.class);
        intent.putExtra(MainActivity.USER_ID, user);
        intent.putExtra(MainActivity.PWD_ID, pwd);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_button : list();
        }
    }
}