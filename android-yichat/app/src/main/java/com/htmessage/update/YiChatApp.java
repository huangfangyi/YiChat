package com.htmessage.update;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;


import com.tencent.mmkv.MMKV;

import java.util.Iterator;
import java.util.List;

/**
 * Created by huangfangyi on 2019/7/20.
 * qq 84543217
 */
public class YiChatApp  extends Application {

    @Override
    public void onCreate() {
        //sdk采用双进程守护,因此不要在守护进程中初始Application
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid, this);
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            return;
        }
        super.onCreate();
        //初始化
        MMKV.initialize(this);

    }



    private  String getAppName(int pID, Context appContext) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }


}
