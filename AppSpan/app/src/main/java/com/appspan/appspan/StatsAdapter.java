package com.appspan.appspan;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class StatsAdapter extends ArrayAdapter<UsageStats> {
    public StatsAdapter(@NonNull Context context, @NonNull List<UsageStats> objects) {
        super(context, R.layout.row_layout, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        PackageManager packageManager= getContext().getPackageManager();


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


        Long appForeground = getItem(position).getTotalTimeInForeground();
        appForeground=appForeground/60000; //milliseconds to minutes
        TextView foregroundTextView = (TextView)rowView.findViewById(R.id.app_foreground_row);
        foregroundTextView.setText(appForeground.toString());

        //getLastTimeUsed()
        //Get the last time this package was used, measured in milliseconds since the epoch.
        //getLastTimeStamp()
        //Get the end of the time range this UsageStats represents, measured in milliseconds since the epoch.

        Long appLastUsage = getItem(position).getLastTimeStamp();
        String last= DateFormat.getDateTimeInstance().format(new Date(appLastUsage));
        TextView lastUsageTextView = (TextView)rowView.findViewById(R.id.app_lastusage_row);
        lastUsageTextView.setText(last);


        Drawable appIcon=null;
        try {
            appIcon = packageManager.getApplicationIcon(pkgName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ImageView appIconView=(ImageView)rowView.findViewById(R.id.app_icon);
        appIconView.setImageDrawable(appIcon);


        return rowView;
    }
}
