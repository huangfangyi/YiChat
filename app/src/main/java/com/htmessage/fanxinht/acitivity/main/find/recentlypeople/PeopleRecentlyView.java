package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.BaseView;

import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 16:30
 * 邮箱:814326663@qq.com
 */
public interface PeopleRecentlyView extends BaseView<PeopleRecentlyPrestener> {
    void showLoadingDialog();
    void hideLoadingDialog();
    void onRequestSuccess(List<JSONObject> peoples);
    void onRequestFailed(String errorMsg);
}
