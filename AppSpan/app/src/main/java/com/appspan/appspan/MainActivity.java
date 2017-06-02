package com.appspan.appspan;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;
import android.app.AppOpsManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;
import java.util.Calendar;
import java.util.List;
import java.util.Collections;
import android.view.View;
import android.widget.TextView;
import android.widget.AdapterView;


public class MainActivity extends AppCompatActivity {

    String interval="Daily";
    List<UsageStats> stats=null;
    TextView mainText=null;
    DataBaseHelper db=null;

    public void setDb() {
        db = new DataBaseHelper(this);
    }

    public DataBaseHelper getDb() {
        return db;
    }

    public void setInterval(String i) {interval=i;}

    public String getInterval(){return interval;}

    /*Displays time interval*/
    public void updateMainText(){this.mainText.setText(interval+" applications usage");}

    /*Gets usage statistics*/
    public void setStats(){
        final UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        stats = getStats(statsManager, getInterval());
    }

    //Renders applications list
    public void renderApps(){
        updateMainText();
        final ListView listView;
        listView = (ListView) findViewById(R.id.apps_list);
        final ListAdapter adapter=new StatsAdapter(this, stats, getDb(), getInterval());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                UsageStats us = (UsageStats) adapterView.getItemAtPosition(position);
                String pkg = us.getPackageName();
                //Toast toast = Toast.makeText(getApplicationContext(), pkg , Toast.LENGTH_SHORT);
                //toast.show();

                //new activity to set limit
                Intent intentOptions = new Intent(getApplicationContext(), OptionsActivity.class);//context, class
                intentOptions.putExtra("package options", pkg);//options activity
                startActivity(intentOptions);
            }
        });
    }

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

    public int checkPermissions(){//0 permission granted
        int usagePermission=opsUsagePermission();
        //Log.wtf("PERMISSION ON/OFF", String.valueOf(usagePermission));
        return  usagePermission;
    }

    /*Asks user for PACKAGE_USAGE_STATS permission,
    * redirects the user to the Access Settings Management*/
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

    /*The maximum duration that the system keeps this data is as follows:
    Daily data: 7 days
    Weekly data: 4 weeks
    Monthly data: 6 months
    Yearly data: 2 years*/

    /*Get statistics from the UsageStatsManager*/
    public List<UsageStats> getStats(UsageStatsManager statsManager, String interval){

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(System.currentTimeMillis());

        int intervalSelection = 0;
        switch (interval) {
            case "Daily":
                start.set(Calendar.DAY_OF_MONTH, -7);
                intervalSelection = UsageStatsManager.INTERVAL_DAILY;
                break;
            case "Weekly":
                start.set(Calendar.MONTH, -1);
                intervalSelection = UsageStatsManager.INTERVAL_WEEKLY;
                break;
            case "Monthly":
                start.set(Calendar.MONTH, -6);
                intervalSelection = UsageStatsManager.INTERVAL_MONTHLY;
                break;
            case "Yearly":
                start.set(Calendar.YEAR, -1);
                intervalSelection = UsageStatsManager.INTERVAL_YEARLY;
                break;
            default:
                start.set(Calendar.DAY_OF_MONTH, -7);
                intervalSelection = UsageStatsManager.INTERVAL_DAILY;
                break;
        }

        List<UsageStats> stats = statsManager.queryUsageStats(intervalSelection, start.getTimeInMillis(), end.getTimeInMillis());
        Collections.reverse(stats);
        return stats;
    }

    /*Sets listeners for the interval selection buttons*/
    public void setButtonListener(Button bt, final String s){
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT);
                toast.show();
                setInterval(s);
                setStats();
                renderApps();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btReload = (Button) findViewById(R.id.button_reload);

        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(MainActivity.this,"RELOADING",Toast.LENGTH_SHORT);
                toast.show();
                setStats();
                renderApps();
        }});


        if(checkPermissions()!=0){
            askForPermission();
        }


        setDb();
        setStats();

        this.mainText=(TextView)findViewById(R.id.main_text);
        updateMainText();

        Button btDay = (Button) findViewById(R.id.button_day);
        Button btWeek = (Button) findViewById(R.id.button_week);
        Button btMonth = (Button) findViewById(R.id.button_month);
        Button btYear = (Button) findViewById(R.id.button_year);

        setButtonListener(btDay, "Daily");
        setButtonListener(btWeek, "Weekly");
        setButtonListener(btMonth, "Monthly");
        setButtonListener(btYear, "Yearly");


        //rendering apps stats list
        renderApps();


    }

    @Override
    protected void onResume() {
        super.onResume();
        renderApps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}






