package com.appspan.appspan;
import android.app.FragmentTransaction;
import android.app.usage.UsageStats;
import android.widget.ListAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.provider.Settings;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.content.Context;
import android.util.Log;
import android.app.AppOpsManager;
import android.content.pm.ApplicationInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import 	java.util.Calendar;
import java.util.List;
import 	java.text.DateFormat;
import java.util.Date;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import  android.support.v4.app.FragmentManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //checks if permission is granted
    //android.permission.PACKAGE_USAGE_STATS is a system-level permission
    //0 permission granted, -1 permission denied
    public int opsUsagePermission(){
        Context context=this.getApplicationContext();
        AppOpsManager appOps = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode;
    }

    public void askForPermission(){
        AlertDialog.Builder permissionDialog = new AlertDialog.Builder(this);
        permissionDialog.setMessage("AppSpan need to access Usage Data, grant permission?")
                .setPositiveButton("Manage Access Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //android.permission.PACKAGE_USAGE_STATS is a system-level permission
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Context context = getApplicationContext();
                        String noUsagePermissionToast = "AppSpan will not be able to get usage statistics";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, noUsagePermissionToast, duration);
                        toast.show();
                    }
                });

        permissionDialog.create();
        permissionDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int usagePermission=opsUsagePermission();
        Log.wtf("PERMISSION ON/OFF", String.valueOf(usagePermission));//0 allowed
        if(usagePermission != 0){
            askForPermission();
        }
        //else {//#############
            Context context = getApplicationContext();
            UsageStatsManager statsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar start = Calendar.getInstance();
            start.set(Calendar.DAY_OF_MONTH, 23);//80 19 70 28 DAILY tot 197
            start.set(Calendar.MONTH, 4);
            start.set(Calendar.YEAR, 2017);
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(System.currentTimeMillis());
            final List<UsageStats> stats = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start.getTimeInMillis(), end.getTimeInMillis());

            for(int i=0; i<stats.size(); ++i)
            {
                String nameTest = stats.get(i).getPackageName();
                Long foreTest = stats.get(i).getTotalTimeInForeground();
                Long lastTest = stats.get(i).getLastTimeUsed();
                foreTest=foreTest/10/10/10/60;//minuti
                Log.wtf("app"+i+"name", String.valueOf(nameTest));
                Log.wtf("app"+i+"fore minuti"+String.valueOf(nameTest), String.valueOf(foreTest));
                String ultima=DateFormat.getDateTimeInstance().format(new Date(lastTest));
                Log.wtf("app"+i+"last"+String.valueOf(nameTest), String.valueOf(ultima));
            }


        //}//###########

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //rendering fragment - useless
        AppsListFragment appsListFragment= new AppsListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, appsListFragment).commit();

        //rendering apps stats
        ListView listView;
        listView = (ListView) findViewById(R.id.apps_list);
        ListAdapter adapter=new StatsAdapter(this, stats);
        listView.setAdapter(adapter);

        //TextView frag=(TextView)findViewById(R.id.main_text);
        //frag.setText("FFFFFFFF main");



    }
}






