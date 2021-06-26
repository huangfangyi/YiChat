package com.htmessage.sdk.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.htmessage.sdk.ProgressConnection;
import com.htmessage.sdk.utils.Logger;

import java.util.Locale;

/**
 * Created by huangfangyi on 2016/12/15.
 * qq 84543217
 */
public class RemoteService extends Service {
    MyBinder myBinder;
    private PendingIntent pintent;
    MyServiceConnection myServiceConnection;
    private NotificationManager manager = null;
    private Notification notification;
    private NotificationCompat.Builder builder;
    @Override
    public void onCreate() {
        Logger.d("RemoteService--->", "onCreate()");
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
     //   handlerTimer.post(runnable);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(1,new Notification());
//        }
    }

     public static final int TYPE_NOTIFICATION=1;
    public static final int TYPE_NOTIFICATION_CANCEL=2;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//       Intent intent1=  new Intent(RemoteService.this, MessageService.class).putExtra("TYPE", MessageService.TYPE_AWAKE);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent1);
//        } else {
//            startService(intent1);
//        }


        this.bindService(new Intent(this, MessageService.class), myServiceConnection, Context.BIND_IMPORTANT);
        if(intent!=null){
//            int type=intent.getIntExtra("TYPE",0);
//            if(type==TYPE_NOTIFICATION){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    setForeground(startId);
//                }
//            }else if(type==TYPE_NOTIFICATION_CANCEL){
//                cancelNotification();
//            }

        }

        setForeground(startId);
        return START_STICKY;
    }
    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (locale.getDefault().toString().contains("zh"))

            return true;
        else
            return false;
    }

     private  void setForeground(int startId){

        manager = (NotificationManager)  getSystemService(Context.NOTIFICATION_SERVICE);
        //为了版本兼容  选择V4包下的NotificationCompat进行构造
        builder = new NotificationCompat.Builder(this);
        String title = "保持消息通知-PUSH";
        String content = "后台进程保活";
        if (!isZh()) {
            title = "Keep alive for Notification";
            content = "From -app";
        }
         Intent intentService=new Intent(this, MessageService.class).putExtra("TYPE", MessageService.TYPE_NTIFICATION_CANCEL);
         PendingIntent pendingIntent = PendingIntent.getService(this,1,intentService,0);

        //  PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.valueOf(userId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(getApplicationInfo().icon);
        notification = builder.build();

        notification.icon = getApplicationInfo().icon;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags = Notification.FLAG_AUTO_CANCEL;



        String id = "channel_04";
        String name ="push";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.setSound(null,null);
            //     Toast.makeText(mContext, mChannel.toString(), Toast.LENGTH_SHORT).show();
            //  Log.i(TAG, mChannel.toString());
            manager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(content)

                    .setSmallIcon( getApplicationInfo().icon).build();



        }




        // manager.notify(Integer.valueOf(userId), notification);//发送通知

        startForeground(notifyId, notification);














    }
    int notifyId=20;
    private void   cancelNotification(){

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
     }


    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Logger.d("RemoteService--->", "主进程service连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.d("RemoteService---->", "唤起主进程");
            // 连接出现了异常断开了，LocalCastielService被杀死了
         Intent intent=   new Intent(RemoteService.this, MessageService.class).putExtra("TYPE", MessageService.TYPE_AWAKE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
               startService(intent);
            }

            RemoteService.this.bindService(new Intent(RemoteService.this, MessageService.class), myServiceConnection, Context.BIND_IMPORTANT);
        }

    }

//    private Handler handlerTimer=new Handler();
//    private Runnable runnable=new Runnable() {
//        @Override
//        public void run() {
//            handlerTimer.postDelayed(runnable,3000);
//            Logger.d("RemoteService","Runnable run()");
//        }
//    };


    class MyBinder extends ProgressConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "";
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }



    @Override
    public void onDestroy() {
        unbindService(myServiceConnection);
        super.onDestroy();
        // Make sure our notification is gone.
    }
}
