package com.htmessage.fanxinht.acitivity.main.about;

import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：FanXinHT0831
 * 类描述：AboutUsBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/25 13:57
 * 邮箱:814326663@qq.com
 */
public interface AboutUsBasePresenter extends BasePresenter {
    void destory();
    void showCopyDialog(String msg);
    void startQQ(String QQ);
    void startCall(String mobile);
}
