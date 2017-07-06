package com.appspan.appspan;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.content.Context;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.Log;

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

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Toast toast=Toast.makeText(context,"1 minute",Toast.LENGTH_LONG);
                            toast.show();
                        } catch (Exception e) {
                            Log.wtf("Exception: ", "1 minute polling failed");
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 60*1000);  // 1 minute
    }
}