package com.htmessage.yichat.acitivity.main.pay.paymentdetails;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.acitivity.BasePresenter;

import java.util.List;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentListBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 16:03
 * 邮箱:814326663@qq.com
 */
public interface PayMentListBasePresenter extends BasePresenter {
    void destory();
    void requestPayMentList(int page);
    List<JSONObject> getPayMentList();
}
