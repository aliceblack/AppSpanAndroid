package com.appspan.appspan;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatsAdapter extends ArrayAdapter<UsageStats> {
    DataBaseHelper DataBase=null;
    String interval=null;

    public StatsAdapter(@NonNull Context context, @NonNull List<UsageStats> objects, DataBaseHelper db, String ntr) {
        super(context, R.layout.row_layout, objects);
        DataBase=db;
        interval=ntr;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        PackageManager packageManager= getContext().getPackageManager();

        //name of the application
        String pkgName = getItem(position).getPackageName();
        TextView nameTextView = (TextView)rowView.findViewById(R.id.app_name_row);
        String appName=null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appName==null){appName=pkgName;}
        nameTextView.setText(appName);


        //total time in foreground
        Long appForeground = getItem(position).getTotalTimeInForeground();
        appForeground=appForeground/60000; //milliseconds to minutes
        TextView foregroundTextView = (TextView)rowView.findViewById(R.id.app_foreground_row);
        foregroundTextView.setText(appForeground.toString()+" minutes");


        //last time this package was used
        Long appLastUsage = getItem(position).getLastTimeStamp();
        String last= DateFormat.getDateTimeInstance().format(new Date(appLastUsage));
        TextView lastUsageTextView = (TextView)rowView.findViewById(R.id.app_lastusage_row);
        lastUsageTextView.setText(last);

        //application icon
        Drawable appIcon=null;
        ImageView appIconView=(ImageView)rowView.findViewById(R.id.app_icon);
        try {
            appIcon = packageManager.getApplicationIcon(pkgName);
            appIconView.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) { //set custom apk icon
            e.printStackTrace();
            appIconView.setImageResource(R.drawable.apk_icon);
        }


        //colored dot reporting time usage status
        Drawable dot=null;
        ImageView dotView=(ImageView)rowView.findViewById(R.id.app_dot);
        dotView.setImageResource(R.drawable.dot_green);

        Long lim=DataBase.getLimit(pkgName);
        if(lim!=-1){
            switch (interval) {
                case "Weekly":
                    lim=lim*7;
                    break;
                case "Monthly":
                    lim=lim*31;
                    break;
                case "Yearly":
                    lim=lim*365;
                    break;
                default:
                    break;
            }
        }


        if( lim !=-1 ){
            if (appForeground == lim){
                dotView.setImageResource(R.drawable.dot_yellow);
            }
            else if (appForeground > lim){
                dotView.setImageResource(R.drawable.dot_red);
            }
            TextView totalLimit = (TextView)rowView.findViewById(R.id.app_foreground_limit);
            totalLimit.setText(" out of "+ lim.toString());
        }


        return rowView;
    }
}
