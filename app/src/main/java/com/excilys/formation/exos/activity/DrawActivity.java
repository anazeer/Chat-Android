package com.excilys.formation.exos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.excilys.formation.exos.view.ParlezVousView;
import com.excilys.formation.exos.R;

/**
 * Circle drawing activity
 */
public class DrawActivity extends Activity {

    private ParlezVousView view;

    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_layout);
        view = (ParlezVousView) findViewById(R.id.circle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.connected_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disconnect:
                Intent main = new Intent(DrawActivity.this, MainActivity.class);
                startActivity(main);
                return true;
            case R.id.cancel:
                return true;
        }
        return true;
    }
}