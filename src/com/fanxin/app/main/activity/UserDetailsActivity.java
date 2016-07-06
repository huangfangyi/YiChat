package com.fanxin.app.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.R;
import com.fanxin.app.db.UserDao;
import com.fanxin.app.main.utils.JSONUtil;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.fanxin.app.ui.BaseActivity;
import com.fanxin.app.ui.ChatActivity;
import com.fanxin.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/6.\
 * QQ:84543217
 */
public class UserDetailsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_userinfo);
        String userInfo = this.getIntent().getStringExtra(FXConstant.KEY_USER_INFO);
        JSONObject userJson = null;
        try {
            userJson = JSONObject.parseObject(userInfo);
        } catch (JSONException e) {

        }

        if (userJson == null) {
            finish();
            return;
        }
        initView(userJson);
        //如果是好友，刷新下资料
        refresh(userJson.getString(FXConstant.JSON_KEY_HXID));
    }

    private void initView(final JSONObject jsonObject) {
        Button btnMsg = (Button) this.findViewById(R.id.btn_msg);
        Button btnAdd = (Button) this.findViewById(R.id.btn_add);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        String hxid = jsonObject.getString(FXConstant.JSON_KEY_HXID);
        String nick = jsonObject.getString(FXConstant.JSON_KEY_NICK);
        String avatar = FXConstant.URL_AVATAR + jsonObject.getString(FXConstant.JSON_KEY_AVATAR);
        Glide.with(this).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(iv_avatar);
        tv_name.setText(nick);
        if ("男".equals(jsonObject.getString(FXConstant.JSON_KEY_SEX))) {
            iv_sex.setImageResource(R.drawable.fx_icon_male);
        } else {
            iv_sex.setImageResource(R.drawable.fx_icon_female);
        }
        //资料是自己
        if (DemoHelper.getInstance().getCurrentUsernName().equals(hxid)) {

            btnMsg.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            return;
        }
        //不是好友的
        if (!DemoHelper.getInstance().getContactList().containsKey(hxid)) {

            btnMsg.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserDetailsActivity.this, AddFriendsFinalActivity.class).putExtra(FXConstant.KEY_USER_INFO, jsonObject.toJSONString()));

                }
            });
            return;
        }
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDetailsActivity.this, ChatActivity.class).putExtra(FXConstant.KEY_USER_INFO, jsonObject.toJSONString()));
            }
        });


    }


    private void refresh(String hxid) {

        if (!DemoHelper.getInstance().getContactList().containsKey(hxid)) {
            return;

        }
        List<Param> parms = new ArrayList<>();
        parms.add(new Param("uid", hxid));

        OkHttpManager.getInstance().post(parms, FXConstant.URL_Search_User, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1) {
                    JSONObject json = jsonObject.getJSONObject("user");
                    //刷新ui
                    initView(json);
                    EaseUser user = JSONUtil.Json2User(json);
                    UserDao dao = new UserDao(UserDetailsActivity.this);
                    dao.saveContact(user);
                    DemoHelper.getInstance().getContactList().put(user.getUsername(), user);

                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }


}