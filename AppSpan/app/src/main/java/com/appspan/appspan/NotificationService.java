package com.appspan.appspan;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
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
import android.os.Build;

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
        //ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        //List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        //String foregroundPkg = tasks.get(0).processName;

        String foregroundPkg=null;
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 100 * 1000, currentTime);
        if(stats != null) {
            long lastUsedAppTime = 0L;
            for (UsageStats usageStats : stats) {
                if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                    foregroundPkg = usageStats.getPackageName();
                }
            }
        }

        currentApp = foregroundPkg;
        //currentApp = "com.appspan.appspan"; //test
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

    //set the current usage for the app in foreground
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
            //Log.wtf("all names", String.valueOf(stats.get(i).getPackageName()));
            if( String.valueOf(stats.get(i).getPackageName()).equals(currentApp)  ) {
                currentUsage = stats.get(i).getTotalTimeInForeground();
                currentUsage=currentUsage/60000;
                Log.wtf("CURRENT MINUTES +++ "+String.valueOf(stats.get(i).getPackageName()), String.valueOf(currentUsage));
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

        //final Context context = getBaseContext();
        //Toast toast = Toast.makeText(context, "Notification Service"+getCurrentApp(), Toast.LENGTH_LONG);
        //Log.wtf("notification service", getCurrentApp());
        //toast.show();

        //notify the user if the limit is reached
        if (getCurrentUsage() >= limit && limit>0 && limit!=null){
            buildNotification(getCurrentUsage());
        }

    }
}