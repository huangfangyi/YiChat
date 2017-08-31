package com.htmessage.fanxinht.acitivity.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by huangfangyi on 2017/6/25.
 * qq 84543217
 */

public class MainPrestener implements MainBasePrester {

    private MainView mainView;
    private Context context;

    public MainPrestener(MainView _mainView){
        this.mainView=_mainView;
        mainView.setPresenter(this);
        context=mainView.getBaseContext();
     }

    @Override
    public void start() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String packageName = context.getPackageName();
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + packageName));
                    context.startActivity(intent);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取VersionCode
     *
     * @return 当前应用的VersionCode
     */
    public String getVersionCode() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = String.valueOf(info.versionCode);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    public void checkVersion() {
            final String version = getVersionCode();
            List<Param> params = new ArrayList<>();
            params.add(new Param("system", "0"));
            new OkHttpUtils(context).post(params, HTConstant.URL_CHECK_UPDATE, new OkHttpUtils.HttpCallBack() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (jsonObject != null) {
                        String serviceVersion = jsonObject.getString("newVersion");
                        String url = jsonObject.getString("url");
                        String info = jsonObject.getString("info");
                        String statue = jsonObject.getString("statue");
                        if (!version.equals(serviceVersion) && (Integer.valueOf(version) < Integer.valueOf(serviceVersion))) {
                            mainView.showUpdateDialog(info, url, statue);

                        }
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                }
            });

    }

}
