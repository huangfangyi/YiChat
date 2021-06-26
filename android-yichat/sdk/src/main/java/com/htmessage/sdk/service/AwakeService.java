package com.htmessage.sdk.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
 public class AwakeService extends NotificationListenerService {

    @Override
    public void onCreate()
    {


       Log.d("AwakeService","onCreate()");
     Intent intent1= new Intent(AwakeService.this,RemoteService.class);

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          startForegroundService(intent1);
       } else {
          startService(intent1);
       }

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          startForeground(1,new Notification());
       }

    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
       // Log.d("AwakeService","onNotificationPosted()");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
      //  Log.d("AwakeService","onNotificationRemoved()");
      }


}