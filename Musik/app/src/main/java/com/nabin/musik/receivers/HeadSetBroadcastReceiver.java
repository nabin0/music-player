package com.nabin.musik.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.nabin.musik.Services.MyMusicPlayerService;

public class HeadSetBroadcastReceiver extends BroadcastReceiver {
    private boolean headsetPluggedIn = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MyMusicPlayerService.class);
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Intent.ACTION_HEADSET_PLUG:
                    int headSetState = intent.getIntExtra("state", -1);
                    if (headSetState == 0 && headsetPluggedIn) {
                        Toast.makeText(context, "Headset Plugged Out", Toast.LENGTH_SHORT).show();
                        headsetPluggedIn = false;
                        serviceIntent.putExtra("actionName", "pause");
                        context.startService(serviceIntent);
                    } else if(headSetState == 1 && !headsetPluggedIn) {
                        Toast.makeText(context, "Headset Plugged In", Toast.LENGTH_SHORT).show();
                        serviceIntent.putExtra("actionName", "play");
                        context.startService(serviceIntent);
                        headsetPluggedIn = true;
                    }
            }
        }
    }
}
