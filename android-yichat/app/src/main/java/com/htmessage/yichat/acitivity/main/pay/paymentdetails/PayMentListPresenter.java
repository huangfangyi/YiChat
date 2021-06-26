package com.htmessage.yichat.acitivity.main.pay.paymentdetails;


import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentListBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 16:03
 * 邮箱:814326663@qq.com
 */
public class PayMentListPresenter implements PayMentListBasePresenter {

    private PaymentListView paymentListView;
    private List<JSONObject> objectList = new ArrayList<>();

    public PayMentListPresenter(PaymentListView paymentListView) {
        this.paymentListView = paymentListView;
        this.paymentListView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void destory() {

    }

    @Override
    public void requestPayMentList(int page) {
        if (page == 1) {
            objectList.clear();
        }
        paymentListView.showProgress();
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        params.add(new Param("currentPage", String.valueOf(page)));
        params.add(new Param("pageSize", String.valueOf("20")));
        new OkHttpUtils(paymentListView.getBaseContext()).post(params, HTConstant.TRANSFER_LOGS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("slj","-----测试:"+jsonObject.toJSONString());
                paymentListView.hintProgress();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        CommonUtils.arrayToJsonList(data, objectList);
                        paymentListView.refreshList();
                        break;
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                paymentListView.hintProgress();
            }
        });
        paymentListView.cancleRefresh();
    }

    @Override
    public List<JSONObject> getPayMentList() {
        return objectList;
    }
}
