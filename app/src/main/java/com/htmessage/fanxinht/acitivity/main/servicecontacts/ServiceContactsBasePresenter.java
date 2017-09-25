package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import com.htmessage.fanxinht.acitivity.BasePresenter;

import java.util.List;

/**
 * 项目名称：PersonalTailor
 * 类描述：ServiceContactsBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 12:58
 * 邮箱:814326663@qq.com
 */
public interface ServiceContactsBasePresenter extends BasePresenter {
    void onDestory();
    List<ServiceUser> sortList(List<ServiceUser> users);
    List<ServiceUser> getUserListFormCache();
}
