package com.htmessage.fanxinht.acitivity.main.feedback;


import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：PersonalTailor
 * 类描述：FeedBackBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/31 16:23
 * 邮箱:814326663@qq.com
 */
public interface FeedBackBasePresenter extends BasePresenter {
    void onDestory();
    void sendFeedBack(String content);
}
