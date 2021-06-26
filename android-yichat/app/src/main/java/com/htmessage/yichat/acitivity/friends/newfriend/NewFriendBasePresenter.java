package com.htmessage.yichat.acitivity.friends.newfriend;

import com.htmessage.yichat.acitivity.BasePresenter;

/**
 * 项目名称：yichat0504
 * 类描述：NewFriendBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/4 9:40
 * 邮箱:814326663@qq.com
 */
public interface NewFriendBasePresenter extends BasePresenter{

    void getData();
    void deleteItem(int position);
    void deleteAll();
    void agreeApply(String fid,int position,String userId);
    void refuseApply(String userId);


}
