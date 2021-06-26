/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.htmessage.sdk.model.CurrentUser;
import com.htmessage.sdk.utils.SystemUtils;

public class HTPreferenceManager {
    /**
     * 保存Preference的name
     */
    private static final String PREFERENCE_NAME = "currentUser";
    private static SharedPreferences mSharedPreferences;
    private static HTPreferenceManager mPreferencemManager;
    private static SharedPreferences.Editor editor;
    private static Context context;

    private HTPreferenceManager(Context cxt) {
        this.context = cxt;
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        mPreferencemManager = new HTPreferenceManager(cxt);

    }

    /**
     * 单例模式，获取instance实例
     *
     * @param
     * @return
     */
    public synchronized static HTPreferenceManager getInstance() {
        if (mPreferencemManager == null) {
            if (context != null) {
                mPreferencemManager = new HTPreferenceManager(context);
            } else {

                throw new RuntimeException("please init first!");
            }

        }

        return mPreferencemManager;
    }


    public void setUser(String username, String password) {
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public CurrentUser getUser() {
        String username = mSharedPreferences.getString("username", null);
        String password = mSharedPreferences.getString("password", null);
        CurrentUser currentUser = null;
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            currentUser = new CurrentUser();
            currentUser.setUsername(username);
            currentUser.setPassword(password);
        }
        return currentUser;
    }

    public void logout() {
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }

    public static long getPingAlarmInterval(Context context, long defaultValue) {
        String networkType = SystemUtils.getCurrentNetworkName(context);
        return (networkType != null) ?
                getLong(context, "ping_alarm_interval_" + networkType, defaultValue) :
                defaultValue;
    }

    private static long getLong(Context context, String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public static long getPingAlarmBackoff(Context context, long defaultValue) {
        String networkType = SystemUtils.getCurrentNetworkName(context);
        return (networkType != null) ?
                getLong(context, "ping_alarm_backoff_" + networkType, defaultValue) :
                defaultValue;
    }


    public static boolean setPingAlarmInterval(Context context, long intervalMillis) {
        String networkType = SystemUtils.getCurrentNetworkName(context);
        return networkType != null && mSharedPreferences.edit()
                .putLong("ping_alarm_interval_" + networkType, intervalMillis)
                .commit();
    }

    public static boolean setPingAlarmBackoff(Context context, long intervalMillis) {
        String networkType = SystemUtils.getCurrentNetworkName(context);
        return networkType != null && mSharedPreferences.edit()
                .putLong("ping_alarm_backoff_" + networkType, intervalMillis)
                .commit();
    }


    public static long getLastConnection(Context context) {
        return getLong(context, "pref_last_connection", -1);
    }

    // TODO why isn't this used?
    public static boolean setLastConnection(Context context) {
        return mSharedPreferences.edit()
                .putLong("pref_last_connection", System.currentTimeMillis())
                .commit();
    }

    public boolean getNotificationShow() {

        return mSharedPreferences.getBoolean("notification", false);

    }


    public void setNotificationShow(boolean show) {
        editor.putBoolean("notification", show);
        editor.commit();
    }
    public boolean isDualProcess() {

        return mSharedPreferences.getBoolean("DualProcess", true);

    }


    public void setDualProcess(boolean isDualProcess) {
        editor.putBoolean("DualProcess", isDualProcess);
        editor.commit();
    }



    public void setIMServer(String imServer){

        editor.putString("imServer", imServer);
        editor.commit();
    }

    public void setDebug(boolean debug){
       // Logger.isDebug=debug;
        editor.putBoolean("debug", debug);
        editor.commit();
    }

    public boolean isDebug(){
        return  mSharedPreferences.getBoolean("debug", true);
    }


    public String getIMServer(){
        return  mSharedPreferences.getString("imServer", null);
    }



    public void setBucket(String bucket){
        editor.putString("bucket", bucket);
        editor.commit();
    }


    public void setEndpoint(String endpoint){
        editor.putString("endpoint", endpoint);
        editor.commit();

    }

    public void setAccessKeyId(String accessKeyId){
        editor.putString("accessKeyId", accessKeyId);
        editor.commit();
    }
    public void setAccessKeySecret(String accessKeySecret){
        editor.putString("accessKeySecret", accessKeySecret);
        editor.commit();
    }


    public String getBucket(){
        return mSharedPreferences.getString("bucket", null);
    }

    public String getAccessKeySecret(){
        return mSharedPreferences.getString("accessKeySecret", null);
    }
    public String getAccessKeyId(){
        return mSharedPreferences.getString("accessKeyId", null);
    }

    public String getEndpoint(){
        return mSharedPreferences.getString("endpoint", null);
    }


    public void setDeviceUpdate(String deviceUpdate){
        editor.putString("deviceUpdate", deviceUpdate);
        editor.commit();
    }
    public void setDeviceGet(String deviceGet){
        editor.putString("deviceGet", deviceGet);
        editor.commit();
    }

    public String getDeviceUpdate(){
        return mSharedPreferences.getString("deviceUpdate", null);
    }

    public String getDeviceGet(){
        return mSharedPreferences.getString("deviceGet", null);
    }
    public void setOssBaseUrl(String ossBaseUrl){

        editor.putString("ossBaseUrl", ossBaseUrl);
        editor.commit();
    }

    public  String getOssBaseUrl(){
        return mSharedPreferences.getString("ossBaseUrl", null);
    }



}
