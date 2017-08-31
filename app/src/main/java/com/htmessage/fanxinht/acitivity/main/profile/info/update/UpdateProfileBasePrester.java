package com.htmessage.fanxinht.acitivity.main.profile.info.update;

/**
 * 项目名称：HTOpen
 * 类描述：UpdateProfileBasePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 13:32
 * 邮箱:814326663@qq.com
 */
public interface UpdateProfileBasePrester {
    void update();
    void updateInfo(String key, String value, String defaultStr);
    String getTitle(int type);
    String getKey(int type);
    void onDestory();
}
