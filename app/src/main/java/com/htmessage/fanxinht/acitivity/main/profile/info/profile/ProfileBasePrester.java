package com.htmessage.fanxinht.acitivity.main.profile.info.profile;

import android.content.Intent;

import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileBasePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 10:50
 * 邮箱:814326663@qq.com
 */
public interface ProfileBasePrester  extends BasePresenter {
    void resgisteRecivier();
    void showPhotoDialog();
    void showSexDialog();
    void result(int requestCode, int resultCode, Intent intent);
    void onDestory();
}
