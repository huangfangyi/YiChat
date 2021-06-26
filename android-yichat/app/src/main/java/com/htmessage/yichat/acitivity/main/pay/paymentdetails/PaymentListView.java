package com.htmessage.yichat.acitivity.main.pay.paymentdetails;


import com.htmessage.yichat.acitivity.BaseView;

/**
 * 项目名称：hanxuan
 * 类描述：PaymentListView 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 16:07
 * 邮箱:814326663@qq.com
 */
public interface PaymentListView extends BaseView<PayMentListPresenter> {
    void showToast(String msg);
    void showProgress();
    void hintProgress();
    void refreshList();
    void cancleRefresh();
}
