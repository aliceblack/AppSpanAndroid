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

        String appName = getItem(position).getPackageName();
        TextView nameTextView = (TextView)rowView.findViewById(R.id.app_name_row);
        nameTextView.setText(appName);

        Long appForeground = getItem(position).getTotalTimeInForeground();
        appForeground=appForeground/10/10/10/60; //minutes
        TextView foregroundTextView = (TextView)rowView.findViewById(R.id.app_foreground_row);
        foregroundTextView.setText(appForeground.toString());

        //getLastTimeUsed()
        //Get the last time this package was used, measured in milliseconds since the epoch.
        //getLastTimeStamp()
        //Get the end of the time range this UsageStats represents, measured in milliseconds since the epoch.

        Long appLastUsage = getItem(position).getLastTimeUsed();//or timestamp
        String last= DateFormat.getDateTimeInstance().format(new Date(appLastUsage));//need testing
        TextView lastUsageTextView = (TextView)rowView.findViewById(R.id.app_lastusage_row);
        lastUsageTextView.setText(last);


        PackageManager packageManager = getContext().getPackageManager();
        Drawable appIcon=null;
        try {
            appIcon = packageManager.getApplicationIcon(appName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ImageView appIconView=(ImageView)rowView.findViewById(R.id.app_icon);
        appIconView.setImageDrawable(appIcon);


        return rowView;
    }
}
