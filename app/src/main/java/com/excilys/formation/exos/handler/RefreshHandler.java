package com.excilys.formation.exos.handler;

import android.os.Handler;
import android.os.Message;

import com.excilys.formation.exos.activity.ListActivity;

/**
 * Handler class for list refreshing
 */
public class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListActivity listActivity = (ListActivity) msg.obj;
            listActivity.refresh();
        }
}