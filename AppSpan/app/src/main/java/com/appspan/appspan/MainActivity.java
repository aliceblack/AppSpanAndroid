package com.appspan.appspan;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.usage.UsageStats;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Vector;
import  android.app.ProgressDialog;
import android.widget.Spinner;
import  android.widget.AdapterView;
import  android.widget.AdapterView.OnItemSelectedListener;
import android.content.DialogInterface.OnClickListener;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper db=null;

    public void setDb() {
        db = new DataBaseHelper(this);
    }

    public DataBaseHelper getDb() {
        return db;
    }

    String interval="Daily";
    List<UsageStats> stats=null;
    TextView mainText=null;

    public void updateMainText(){this.mainText.setText(interval+" applications usage");}


    public void setInterval(String i) {interval=i;}

    public String getInterval(){return interval;}

    public void setStats(){
        final UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        stats=getStats(statsManager, getInterval());
    }

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
                Toast toast = Toast.makeText(getApplicationContext(), pkg , Toast.LENGTH_SHORT);
                toast.show();
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

    /*class ListTask extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            AlertDialog dialog = new ProgressDialog(getApplicationContext());
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override//background
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int usagePermission=opsUsagePermission();
        Log.wtf("PERMISSION ON/OFF", String.valueOf(usagePermission));
        if(usagePermission != 0){ //0 permission granted
            askForPermission();
        }

        setDb();

        Context context = getApplicationContext();
        final UsageStatsManager statsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        setStats();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//before all the findViewById

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



        //db.addLimit("com.android.chrome",60L);
        //db.addLimit("com.google.android.youtube",20L);
        //db.addLimit("com.google.android.music",2L);
        //db.addLimit("com.amazon.avod.thirdpartyclient",10L);

        //Long lim1 = db.getLimit("app1"); //Log.wtf("limitapp1=", lim1.toString());

        //rendering fragment - useless
        //AppsListFragment appsListFragment= new AppsListFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.main_container, appsListFragment).commit();

        //db.open on resume
        //db.close(); on pause // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx-------------

    }


}






