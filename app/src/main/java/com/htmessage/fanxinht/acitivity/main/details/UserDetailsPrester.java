package com.htmessage.fanxinht.acitivity.main.details;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.domain.UserDao;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：UserDetailsPrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 11:40
 * 邮箱:814326663@qq.com
 */
public class UserDetailsPrester implements UserDetailsBasePrester {

    private UserDetailsView detailsView;

    public UserDetailsPrester(UserDetailsView detailsView) {
        this.detailsView = detailsView;
        this.detailsView.setPresenter(this);
    }

    @Override
    public void onDestory() {
        detailsView = null;
    }

    @Override
    public void refreshInfo(final String userId, final boolean backTask) {
        if (!backTask){
            detailsView.showDialog();
        }
        List<Param> parms = new ArrayList<>();
        parms.add(new Param("userId", userId));
        new OkHttpUtils(detailsView.getBaseContext()).post(parms, HTConstant.URL_Get_UserInfo, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (!backTask){
                    detailsView.hintDialog();
                }
                switch (code){
                    case 1:
                        JSONObject json = jsonObject.getJSONObject("user");
                        //刷新ui
                        if (isFriend(userId)) {
                            User user = CommonUtils.Json2User(json);
                            UserDao dao = new UserDao(detailsView.getBaseContext());
                            dao.saveContact(user);
                            ContactsManager.getInstance().getContactList().put(user.getUsername(), user);
                        }
                        detailsView.showUi(json);
                        break;
                    default:
                        detailsView.hintDialog();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                detailsView.onRefreshFailed(errorMsg);
                if (!backTask){
                    detailsView.hintDialog();
                }
            }
        });
    }

    @Override
    public boolean isMe(String userId) {
        return HTApp.getInstance().getUsername().equals(userId);
    }

    @Override
    public boolean isFriend(String userId) {
        return ContactsManager.getInstance().getContactList().containsKey(userId);
    }

    @Override
    public void start() {

    }
}
