package com.excilys.android.formation.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.excilys.android.formation.R;

/**
 * Useful functions
 */
public class Utils {

    private Context context;
    private Resources resources;

    public Utils(Context context) {
        this.context = context;
        this.resources = context.getResources();
    }

    /**
     * Get a message depending on the server status response
     * @param status the server status response
     * @return the corresponding personalized message
     */
    public String getRegistrationStatusText(String status) {
        if (status == null) {
            return resources.getString(R.string.register_null);
        }
        switch (status) {
            case "200": return resources.getString(R.string.register_success);
            case "400": return resources.getString(R.string.register_exist);
            default:    return resources.getString(R.string.register_fail);
        }
    }

    /**
     * Check whether the device is connected to the network
     * @return true if the network is accessible
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Get a message depending on the server status response
     * @param status the server status response
     * @return the corresponding personalized message
     */
    public String getMessageStatusText(String status) {
        switch (status) {
            case "200": return resources.getString(R.string.msg_success);
            case "400": return resources.getString(R.string.msg_exist);
            case "401": return resources.getString(R.string.msg_illegal);
            case "413": return resources.getString(R.string.msg_big);
            default:    return resources.getString(R.string.msg_failure);
        }
    }

}