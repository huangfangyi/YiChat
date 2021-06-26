package com.htmessage.yichat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.login.LoginActivity;
import com.htmessage.yichat.manager.NotifierManager;
import com.htmessage.yichat.utils.LoggerUtils;
 import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




public class HTApp extends Application {
    private static Context applicationContext;
    private static HTApp instance;
    private List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        //sdk采用双进程守护,因此不要在守护进程中初始Application
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid, this);
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            return;
        }
        super.onCreate();
        applicationContext = this;
        instance = this;
        MMKV.initialize(this);
        NotifierManager.init(applicationContext);
        CrashReport.initCrashReport(applicationContext, Constant.BUGLY_KEY, false);  //异常上报
        HTClientHelper.init(applicationContext);
        //初始化LOG工具类
        LoggerUtils.isDebug(BuildConfig.DEBUG);
        closeAndroidPDialog();
        Fresco.initialize(applicationContext);



    }


    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Context getContext() {
        return applicationContext;
    }

    public static HTApp getInstance() {
        return instance;
    }

    public String getUsername() {

        return UserManager.get().getMyUserId();
    }

    public String getUserNick() {

        return UserManager.get().getMyNick();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    private static String getAppName(int pID, Context appContext) {
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

    public String getDirFilePath() {
        File file = new File(HTConstant.DIR_AVATAR);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator;
    }

    public String getImageDirFilePath() {
        File file = new File(HTConstant.DIR_AVATAR + "images/");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator;
    }

    public String getVideoPath() {
        File file = new File(HTConstant.DIR_AVATAR + "video/");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator;
    }


    public void logoutApp(int type) {
        //type=1,被提出，0主动退出
        //清除登录者信息
        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        UserManager.get().clearMyData();
        NotifierManager.getInstance().cancel();
        finishActivities();
        Intent intent=new Intent(applicationContext, LoginActivity.class);
        intent.putExtra("type",type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        applicationContext.startActivity(intent);
      //  JPushInterface.stopPush(getApplicationContext());




    }


    public void saveActivity(Activity activity) {
        if (activity != null) {
            activities.add(activity);
            Log.d("activities----",activities.size()+"");
        }

    }

    public void finishActivities() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }

    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }


}
