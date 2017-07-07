package com.appspan.appspan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import java.util.Calendar;
import android.app.NotificationManager;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

public class NotificationService extends Service {
    public DataBaseHelper db = null;
    public String currentApp = null;
    public Long currentUsage = 0L;
    public Long limit = 0L;

    public void setDb(){
        db = new DataBaseHelper(getBaseContext());
    }

    public void setLimit(){
        limit = db.getLimit(getCurrentApp());
    }

    public DataBaseHelper getDb(){
        return db;
    }

    public Long getLimit(){
        return limit;
    }

    public Long getCurrentUsage(){
        return currentUsage;
    }


    public String getCurrentApp(){
        return currentApp;
    }

    public void setCurrentApp(){
        //gets wich application is being used
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if ( !sortedMap.isEmpty()) {
                currentApp = sortedMap.get(sortedMap.lastKey()).getPackageName();
            }
        }
    }


    //build and show a notification
    public void buildNotification(Long currentUsage){

        //name of the application
        PackageManager packageManager= getApplicationContext().getPackageManager();
        String appName=null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(currentApp, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appName==null){appName=currentApp;}

        //notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.small_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle("Warning "+currentApp)
                        .setContentText(appName+": "+currentUsage+" minutes out of "+getLimit())
                        .setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    //sets the current usage for the app in foreground
    public Long setCurrentUsage(){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(System.currentTimeMillis());
        start.set(Calendar.DAY_OF_MONTH, -7);
        final UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start.getTimeInMillis(), end.getTimeInMillis());
        Collections.reverse(stats); //today first

        Log.wtf("CURRENT APP -- ", String.valueOf(currentApp));
        for(int i=0; i<stats.size(); ++i){
            if( String.valueOf(stats.get(i).getPackageName()).equals(currentApp)  ) {
                currentUsage = stats.get(i).getTotalTimeInForeground();
                currentUsage = currentUsage/60000;
                //Log.wtf("Current app usage: "+String.valueOf(stats.get(i).getPackageName()), String.valueOf(currentUsage));
                break;
            }
        }
        return currentUsage;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setDb();
        setCurrentApp();
        setCurrentUsage();
        setLimit();

        //notify the user if the limit is reached, only if the limit exists
        if (getCurrentUsage() >= getLimit() && getLimit()!=-1 ){
            buildNotification(getCurrentUsage());
        }

    }
}