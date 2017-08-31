package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyBasePrestener 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 16:28
 * 邮箱:814326663@qq.com
 */
public interface PeopleRecentlyBasePrestener extends BasePresenter {
    void requestData(int page,int pageSize,boolean loadMore);
    void onListClickListener(JSONObject object);
    void onDestory();
}
