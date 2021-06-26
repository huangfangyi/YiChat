package com.htmessage.yichat.acitivity.chat.card;

import com.htmessage.yichat.acitivity.BaseView;
import com.htmessage.yichat.domain.User;

import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：CheckCardView 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/17 11:56
 * 邮箱:814326663@qq.com
 */
public interface CheckCardView extends BaseView<CheckCardPresenter> {
    void showToast(Object object);
    void refreshList(List<User> users);
}
