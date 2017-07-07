package com.appspan.appspan;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import 	android.support.v4.app.NotificationCompat.Builder;
import android.app.Notification;

import java.util.List;

public class MonitorCurrentApp extends Activity {

    DataBaseHelper db = null;
    String currentApp = null;

    public void setDb(){
        db = new DataBaseHelper(this);
    }

    public void setCurrentApp(){
        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        currentApp = tasks.get(0).processName;

    }

    public DataBaseHelper getDb(){
        return db;
    }

    public String getCurrentApp(){
        return currentApp;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCurrentApp();
        //String currentAppPkg = getCurrentApp();

        //Intent monitorIntent = getIntent();
        //get package name from Monitor Service
        //final String pkg = monitorIntent.getExtras().getString("package name");

        Toast curr=Toast.makeText(getApplicationContext(), "current ", Toast.LENGTH_LONG);
        curr.show();


        //Long limit = getDb().getLimit(currentApp);
        /*final UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(System.currentTimeMillis());
        start.set(Calendar.DAY_OF_MONTH, -7);

        List<UsageStats> stats = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start.getTimeInMillis(), end.getTimeInMillis());

        Long currentUsage = 0L;
        for(int i=0; i<stats.size(); ++i){
            if( stats.get(i).getPackageName() == currentApp ) {
                currentUsage = stats.get(i).getTotalTimeInForeground();
            }
            break;
        }

        if (currentUsage > limit){
            Toast LIMITSS=Toast.makeText(context, "limit for"+currentApp, Toast.LENGTH_LONG);
            LIMITSS.show();
            //NotificationCompat.Builder mBuilder =
            //        new NotificationCompat.Builder(this)
            //                .setSmallIcon(R.drawable.dot_red)
            //                .setContentTitle("My notification")
            //                .setContentText("Hello World!");

            //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //NotificationManager.notify().mNotificationManager.notify(001, mBuilder.build());
        }*/


    }
}
