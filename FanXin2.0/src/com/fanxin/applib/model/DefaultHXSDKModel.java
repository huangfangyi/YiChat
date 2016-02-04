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
package com.fanxin.applib.model;

/**
 * UI Demo HX Model implementation
 */

import com.fanxin.app.db.UserDao;
import com.fanxin.applib.utils.HXPreferenceUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * HuanXin default SDK Model implementation
 * @author easemob
 *
 */
public class DefaultHXSDKModel extends HXSDKModel{
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PWD = "pwd";
    UserDao dao = null;
    protected Context context = null;
    
    public DefaultHXSDKModel(Context ctx){
        context = ctx;
        HXPreferenceUtils.init(context);
    }
    
    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {
        // TODO Auto-generated method stub
        HXPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
    }

    @Override
    public boolean getSettingMsgNotification() {
        // TODO Auto-generated method stub
        return HXPreferenceUtils.getInstance().getSettingMsgNotification();
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {
        // TODO Auto-generated method stub
        HXPreferenceUtils.getInstance().setSettingMsgSound(paramBoolean);
    }

    @Override
    public boolean getSettingMsgSound() {
        // TODO Auto-generated method stub
        return HXPreferenceUtils.getInstance().getSettingMsgSound();
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {
        // TODO Auto-generated method stub
        HXPreferenceUtils.getInstance().setSettingMsgVibrate(paramBoolean);
    }

    @Override
    public boolean getSettingMsgVibrate() {
        // TODO Auto-generated method stub
        return HXPreferenceUtils.getInstance().getSettingMsgVibrate();
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        // TODO Auto-generated method stub
        HXPreferenceUtils.getInstance().setSettingMsgSpeaker(paramBoolean);
    }

    @Override
    public boolean getSettingMsgSpeaker() {
        // TODO Auto-generated method stub
        return HXPreferenceUtils.getInstance().getSettingMsgSpeaker();
    }

    @Override
    public boolean getUseHXRoster() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean saveHXId(String hxId) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, hxId).commit();
    }

    @Override
    public String getHXId() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }

    @Override
    public boolean savePassword(String pwd) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_PWD, pwd).commit();    
    }

    @Override
    public String getPwd() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_PWD, null);
    }

    @Override
    public String getAppProcessName() {
        // TODO Auto-generated method stub
        return null;
    }
}
