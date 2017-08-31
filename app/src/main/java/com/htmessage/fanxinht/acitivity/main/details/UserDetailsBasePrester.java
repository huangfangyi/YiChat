package com.htmessage.fanxinht.acitivity.main.details;

import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：yichat0504
 * 类描述：UserDetailsBasePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 11:35
 * 邮箱:814326663@qq.com
 */
public interface UserDetailsBasePrester extends BasePresenter {
        void onDestory();
        void refreshInfo(String userId,boolean backTask);
        boolean isMe(String userId);
        boolean isFriend(String userId);
}
