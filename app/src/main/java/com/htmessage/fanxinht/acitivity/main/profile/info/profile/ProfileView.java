package com.htmessage.fanxinht.acitivity.main.profile.info.profile;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 11:00
 * 邮箱:814326663@qq.com
 */
public interface ProfileView extends BaseView<ProfilePrester> {
    void onNickUpdate(String nick, boolean isHang);
    void onSexUpdate(int sex, boolean isHang);
    void onSignUpdate(String sign, boolean isHang);
    void onAvatarUpdate(String avatar, boolean isHang);
    void onRegionUpdate(String region, boolean isHang);
    void onFxidUpdate(String fxid, boolean isHang);
    void onUpdateSuccess(String msg);
    void onUpdateFailed(String error);
    JSONObject getUserJson();
}
