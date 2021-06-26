package com.htmessage.yichat.acitivity.friends.newfriend;

import com.alibaba.fastjson.JSONArray;
import com.htmessage.yichat.acitivity.BaseView;

/**
 * Created by huangfangyi on 2019/7/29.
 * qq 84543217
 */
public interface NewFriendView extends BaseView<NewFriendPresenter> {
    void showToast(int resId);
    void initRecyclerView(JSONArray data);
    void refreshRecyclerView();
    void changeAgreeButton(String fid);
}
