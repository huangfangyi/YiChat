package com.htmessage.fanxinht.acitivity.main.notice;


import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * 项目名称：PersonalTailor
 * 类描述：AllNoticeView 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 16:52
 * 邮箱:814326663@qq.com
 */
public interface AllNoticeView extends BaseView<AllNoticePresenter> {
    void showToast(String msg);
    void cancleRefresh();
    void RefreshList();
}
