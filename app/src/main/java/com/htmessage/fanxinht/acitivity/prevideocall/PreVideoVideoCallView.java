package com.htmessage.fanxinht.acitivity.prevideocall;

import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.acitivity.BaseView;
import java.util.ArrayList;

/**
 * 项目名称：yichat0504
 * 类描述：PreVideoVideoCallView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/11 11:00
 * 邮箱:814326663@qq.com
 */
public interface PreVideoVideoCallView extends BaseView<PreVideoCallPrestener> {
    String getGroupId();
    void  reFreshView(ArrayList<User> exitUsers, ArrayList<String> userIds, ArrayList<User> users);
}
