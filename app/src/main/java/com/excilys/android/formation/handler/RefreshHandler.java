package com.excilys.android.formation.handler;

import android.os.Handler;
import android.os.Message;

import com.excilys.android.formation.activity.ListActivity;

/**
 * Handler class for list refreshing
 */
public class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListActivity listActivity = (ListActivity) msg.obj;
            listActivity.list();
        }
}