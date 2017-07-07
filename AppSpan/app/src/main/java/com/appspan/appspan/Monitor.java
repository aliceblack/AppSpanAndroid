package com.appspan.appspan;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            stopService(new Intent(context, NotificationService.class));
                            startService(new Intent(context, NotificationService.class));

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