package com.htmessage.sdk.service;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by huangfangyi on 2017/2/26.
 * qq 84543217
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepAliveService extends JobService {
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            Toast.makeText(MyJobService.this, "MyJobService", Toast.LENGTH_SHORT).show();
            JobParameters param = (JobParameters) msg.obj;
            jobFinished(param, true);
           // startService(new Intent(KeepAliveService.this,MessageService2.class));
          Intent intent= new Intent(KeepAliveService.this,RemoteService.class) ;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 startForegroundService(intent);
            } else {
                 startService(intent);
            }
            Log.d("KeepAliveService","handleMessage");
  //            startActivity(intent);
            return true;
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handlerTimer.postDelayed(runnable,0);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("KeepAliveService","onStartJob");
        Message m = Message.obtain();
        m.obj = params;
        handler.sendMessage(m);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1,new Notification());
        }
    }

    private Handler handlerTimer=new Handler();
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            handlerTimer.postDelayed(runnable,3000);
            Log.d("KeepAliveService","Runnable run()");
        }
    };


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("KeepAliveService","onStopJob");
        handler.removeCallbacksAndMessages(null);
        return false;
    }
}

