package com.htmessage.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.htmessage.sdk.service.AwakeService;

/**
 * Created by huangfangyi on 2017/3/11.
 * qq 84543217
 */

public class SystemReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
       Intent intent1= new Intent(context, AwakeService.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }


        if(       intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("SystemReceiver","ACTION_BOOT_COMPLETED");
        }else if( intent.getAction().equals(Intent.ACTION_USER_PRESENT)){

            Log.d("SystemReceiver","ACTION_USER_PRESENT");
        }else if( intent.getAction().equals(Intent.ACTION_SCREEN_ON)){

            Log.d("SystemReceiver","ACTION_SCREEN_ON");
        }

    }
}
