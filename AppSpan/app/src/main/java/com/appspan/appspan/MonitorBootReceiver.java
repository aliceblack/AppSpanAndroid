package com.appspan.appspan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MonitorBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Toast toast=Toast.makeText(context,"BroadcastReceiver",Toast.LENGTH_LONG);
            toast.show();

            //starts a service for monitoring current apps usage
            Intent monitorInten = new Intent(context, Monitor.class);
            context.startService(monitorInten);
        }
    }

}