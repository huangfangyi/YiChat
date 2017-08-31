package com.htmessage.fanxinht.acitivity.addfriends.add.next;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * 项目名称：HTOpen
 * 类描述：AddFriendNextView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 17:24
 * 邮箱:814326663@qq.com
 */
public interface AddFriendNextView extends BaseView<AddFriendNextPrestener> {
    String getInputString();
    void onSearchSuccess(JSONObject object);
    void onSearchFailed(String error);
}
