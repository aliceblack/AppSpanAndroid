package com.appspan.appspan;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.content.Context;

public class Monitor extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}