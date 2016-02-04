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
 * HX SDK app model which will manage the user data and preferences
 * @author easemob
 *
 */
public abstract class HXSDKModel {
    public abstract void setSettingMsgNotification(boolean paramBoolean);
    public abstract boolean getSettingMsgNotification();

    public abstract void setSettingMsgSound(boolean paramBoolean);
    public abstract boolean getSettingMsgSound();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);
    public abstract boolean getSettingMsgVibrate();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);
    public abstract boolean getSettingMsgSpeaker();
   
    public abstract boolean saveHXId(String hxId);
    public abstract String getHXId();
    
    public abstract boolean savePassword(String pwd);
    public abstract String getPwd();
    
    /**
     * 返回application所在的process name,默认是包名
     * @return
     */
    public abstract String getAppProcessName();
    /**
     * 是否总是接收好友邀请
     * @return
     */
    public boolean getAcceptInvitationAlways(){
        return false;
    }
    
    /**
     * 是否需要环信好友关系，默认是false
     * @return
     */
    public boolean getUseHXRoster(){
        return false;
    }
    
    /**
     * 是否需要已读回执
     * @return
     */
    public boolean getRequireReadAck(){
        return true;
    }
    
    /**
     * 是否需要已送达回执
     * @return
     */
    public boolean getRequireDeliveryAck(){
        return false;
    }
    
    /**
     * 是否运行在sandbox测试环境. 默认是关掉的
     * 设置sandbox 测试环境
     * 建议开发者开发时设置此模式
     */
    public boolean isSandboxMode(){
        return false;
    }
    
    /**
     * 是否设置debug模式
     * @return
     */
    public boolean isDebugMode(){
        return false;
    }
}
