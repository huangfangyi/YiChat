package com.htmessage.fanxinht.acitivity.addfriends.newfriend;

import android.content.Context;
import com.htmessage.fanxinht.domain.InviteMessage;
import com.htmessage.fanxinht.acitivity.BasePresenter;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：NewFriendBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/4 9:40
 * 邮箱:814326663@qq.com
 */
public interface NewFriendBasePresenter extends BasePresenter{
    List<InviteMessage> getAllInviteMessage();
    void registerRecivier();
    void startActivity(Context context, Class clazz);
    void saveUnreadMessageCount(int count);
    void refresh();
    void onDestory();
}
