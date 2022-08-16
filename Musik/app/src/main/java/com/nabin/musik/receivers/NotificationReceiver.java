package com.nabin.musik.receivers;

import static com.nabin.musik.application.App.ACTION_NEXT;
import static com.nabin.musik.application.App.ACTION_PLAY;
import static com.nabin.musik.application.App.ACTION_PREVIOUS;
import static com.nabin.musik.application.App.ACTION_STOP_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.nabin.musik.Services.MyMusicPlayerService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();

        Intent serviceIntent = new Intent(context, MyMusicPlayerService.class);
        if(actionName != null){
            switch (actionName){
                case ACTION_PLAY:
                    serviceIntent.putExtra("actionName", "playPause");
                    context.startService(serviceIntent);
                    break;
                case ACTION_NEXT:
                    serviceIntent.putExtra("actionName", "next");
                    context.startService(serviceIntent);
                    break;
                case ACTION_PREVIOUS:
                    serviceIntent.putExtra("actionName", "previous");
                    context.startService(serviceIntent);
                    break;
                case ACTION_STOP_SERVICE:
                    serviceIntent.putExtra("actionName", "stopService");
                    context.startService(serviceIntent);
                    break;
            }
        }
    }
}
