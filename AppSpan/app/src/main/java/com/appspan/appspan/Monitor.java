package com.appspan.appspan;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.content.Context;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.Log;
import android.app.ActivityManager;
import java.util.List;

public class Monitor extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context=getApplicationContext();
        Toast toast=Toast.makeText(context,"MONITOR ---- SERVICE",Toast.LENGTH_LONG);
        toast.show();

        //final Intent monitorIntent = new Intent(context, NotificationService.class);

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            Toast curr=Toast.makeText(context, "1min ", Toast.LENGTH_SHORT);
                            curr.show();


                            //Intent intentCurrent = new Intent(getApplicationContext(), MonitorCurrentApp.class);//context, class
                            //intentCurrent.putExtra("package name", currentApp);
                            //intentCurrent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//MUST
                            //getApplicationContext().startActivity(intentCurrent);

                            stopService(new Intent(context, NotificationService.class));
                            startService(new Intent(context, NotificationService.class));
                            //context.startService(monitorIntent);

                        } catch (Exception e) {
                            Log.wtf("Exception: ", "1 minute polling failed");
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 60*1000);  // 1 minute x1000


    }
}