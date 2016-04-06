package com.excilys.formation.exos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Receiver for action power and sms
 */
public class PowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "Power connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "Sms received", Toast.LENGTH_SHORT).show();
        }
    }
}