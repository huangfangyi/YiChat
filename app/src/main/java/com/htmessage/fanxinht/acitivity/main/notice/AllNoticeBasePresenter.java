package com.htmessage.fanxinht.acitivity.main.notice;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.BasePresenter;

import java.util.List;

/**
 * 项目名称：YiChat
 * 类描述：AllNoticeBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 16:53
 * 邮箱:814326663@qq.com
 */
public interface AllNoticeBasePresenter extends BasePresenter {

    List<JSONObject> getAllNotice();
    void onDestory();
    void onRefresh();
    void onLoadMore(int page);
}
