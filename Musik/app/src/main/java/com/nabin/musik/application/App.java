package com.nabin.musik.application;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1 = "notification_channel_1";
    public static final String CHANNEL_2 = "channel_2";
    public static final String ACTION_PLAY =  "com.nabin.musik.action_play";
    public static final String ACTION_PREVIOUS = "com.nabin.musik.action_previous";
    public static final String ACTION_NEXT = "com.nabin.musik.action_next";
    public static final String ACTION_STOP_SERVICE = "com.nabin.musik.action_stop_servce";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            /*
            Create Notificatin channels
             */

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1, "Channel(1)", NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("Channel1...");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2, "Channel(2)", NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("Channel2...");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
}
