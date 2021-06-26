package com.htmessage.yichat.acitivity.chat.card;

import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.domain.User;

import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：CheckCardBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/17 11:56
 * 邮箱:814326663@qq.com
 */
public interface CheckCardBasePresenter extends BasePresenter {
    List<User> getContents();
    void destory();
}
