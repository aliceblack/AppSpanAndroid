package com.appspan.appspan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MonitorBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Toast toast=Toast.makeText(context,"Monitor boot",Toast.LENGTH_LONG);
            toast.show();

            Intent monitorInten = new Intent(context, Monitor.class);
            //monitorInten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(monitorInten);
        }
    }

}