package com.appspan.appspan;

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
import android.widget.Toast;

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}






