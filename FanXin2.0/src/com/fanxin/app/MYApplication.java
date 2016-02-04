/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanxin.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.fanxin.app.fx.others.TopUser;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class MYApplication extends Application {
    public static String last_time = "0";
    public List<JSONObject> list = new ArrayList<JSONObject>();
    public static int page = 0;
    public static Context applicationContext;
    private static MYApplication instance;
    // login user name
    public final String PREF_USERNAME = "username";
    
//    private   String myNick="";
//    private   String myAvatar="";
    

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
    private List<Activity> aList = new ArrayList<Activity>();
    private String time="";
     private String myHxid="";
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related
         *         APIs after calling onInit, otherwise false.
         * 
         *         环信初始化SDK帮助函数
         *         返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         *         for example: 例子：
         * 
         *         public class DemoHXSDKHelper extends HXSDKHelper
         * 
         *         HXHelper = new DemoHXSDKHelper();
         *         if(HXHelper.onInit(context)){ // do HuanXin related work }
         */
        hxSDKHelper.onInit(applicationContext);
        Fresco.initialize(this);
        initImage();
        getNowTime();
        CrashReport.initCrashReport(getApplicationContext(), "900019446", false);

    }
  
    private void getNowTime() {
        String hxid = getUserName();
        if (hxid == null)
            return;
        Map<String, String> map = new HashMap<String, String>();
        map.put("hxid", hxid);
        LoadDataFromServer task = new LoadDataFromServer(
                getApplicationContext(), Constant.URL_UPDATETIME, map);
        task.getData(new DataCallBack(){

            @Override
            public void onDataCallBack(JSONObject data) {
                 
            }
            
        });;

    }
    
     
    public String getTime(){
        return time;
    }
    public static MYApplication getInstance() {
        return instance;
    }
    
    public void setTime(String time){
        this.time=time;
    }
    @SuppressWarnings("deprecation")
    public void initImage() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                // 设置图片下载期间显示的图�?
                .showImageForEmptyUri(R.drawable.ic_empty)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_error)
                // 设置图片加载或解码过程中发生错误显示的图�?
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存�?
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图
                .build(); // 创建配置过得DisplayImageOption对象

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache
                // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) // 缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(
                        new BaseImageDownloader(getApplicationContext(),
                                5 * 1000, 30 * 1000)) // connectTimeout (5 s),
                                                        // readTimeout (30
                                                        // s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();// 开始构建
        ImageLoader.getInstance().init(config);

    }
    /**
     * 获取内存中好友user list
     * 
     * @return
     */
    public Map<String, User> getContactList() {
        return hxSDKHelper.getContactList();
    }

    /**
     * 获取内存中置顶好友user list
     * 
     * @return
     */

    public Map<String, TopUser> getTopUserList() {
        return hxSDKHelper.getTopUserList();
    }

    /**
     * 设置好友user list到内存中
     * 
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxSDKHelper.setContactList(contactList);
    }

    /**
     * 设置置顶好友到内存中
     * 
     * @param contactList
     */
    public void setTopUserList(Map<String, TopUser> contactList) {
        hxSDKHelper.setTopUserList(contactList);
    }

    /**
     * 获取当前登陆用户名
     * 
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     * 
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     * 
     * @param user
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     * 
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(emCallBack);
        finishActivitys();
        
    }

    public void addActivity(Activity activity) {
        if (!aList.contains(activity)) {

            aList.add(activity);
        }

    }

    public void finishActivitys() {

        for (int i = 0; i < aList.size(); i++) {

            if (!aList.get(i).isFinishing())
                aList.get(i).finish();

        }
    }
}
