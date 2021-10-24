package com.kmt.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class MyApplication extends Application {
    public static final String CHANNEL_ID ="channel_player";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"player_notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            NotificationManagerCompat manager=NotificationManagerCompat.from(this);
            manager.createNotificationChannel(channel);
        }
    }
}
