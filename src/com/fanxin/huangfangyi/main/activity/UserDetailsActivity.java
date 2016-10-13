package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.JSONUtil;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.EaseConstant;
import com.fanxin.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/6.\
 * QQ:84543217
 */
public class UserDetailsActivity extends BaseActivity {
    /**
     * 用户详情页接收两种传值
     * 1：用户完整资料的JSON字符串-userInfo-这种情况如果是好友进行刷新
     * 2：只传用户的hxid，这种情况直接从网络取数据显示-如果是好友，刷新资料
     */

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_userinfo);
        String hxid = this.getIntent().getStringExtra(FXConstant.JSON_KEY_HXID);
        if (hxid != null) {
            refresh(hxid, true);
            return;
        }

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
        if (DemoHelper.getInstance().getContactList().containsKey(userJson.getString(FXConstant.JSON_KEY_HXID))) {
            refresh(userJson.getString(FXConstant.JSON_KEY_HXID), false);
        }

    }

    private void initView(final JSONObject jsonObject) {
        Button btnMsg = (Button) this.findViewById(R.id.btn_msg);
        Button btnAdd = (Button) this.findViewById(R.id.btn_add);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        TextView tv_mobile = (TextView) this.findViewById(R.id.tv_mobile);
        TextView tv_sign = (TextView) this.findViewById(R.id.tv_sign);
        TextView tv_region = (TextView) this.findViewById(R.id.tv_region);
        TextView tv_mixin = (TextView) this.findViewById(R.id.tv_mixin);

        RelativeLayout re_mobile = (RelativeLayout) this.findViewById(R.id.re_mobile);
        String hxid = jsonObject.getString(FXConstant.JSON_KEY_HXID);
        String nick = jsonObject.getString(FXConstant.JSON_KEY_NICK);
        String avatar = FXConstant.URL_AVATAR + jsonObject.getString(FXConstant.JSON_KEY_AVATAR);
        String sign = jsonObject.getString(FXConstant.JSON_KEY_SIGN);
        String fxid = jsonObject.getString(FXConstant.JSON_KEY_FXID);
        String tel = jsonObject.getString(FXConstant.JSON_KEY_TEL);
        Glide.with(this).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(iv_avatar);
        tv_name.setText(nick);
        tv_sign.setText(sign);
        String province = jsonObject.getString(FXConstant.JSON_KEY_PROVINCE);
        String city = jsonObject.getString(FXConstant.JSON_KEY_CITY);
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
            tv_region.setText(province + " " + city);
            if (province.equals(city)) {
                tv_region.setText(city);
            }
        } else {

            tv_region.setText("未设置");
        }

//        String is_show_tel=jsonObject.getString(FXConstant.JSON_KEY_EN_SHOW_TEL);
//        if("1".equals(is_show_tel)){

        tv_mobile.setText(tel);
//        }else {
//
//            re_mobile.setVisibility(View.GONE);
//        }

        if (!TextUtils.isEmpty(fxid)) {

            tv_mixin.setText("凡信号： " + fxid);
        } else {
            tv_mixin.setText("凡信号： " + "未设置");

        }

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
                startActivity(new Intent(UserDetailsActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, jsonObject.getString(FXConstant.JSON_KEY_HXID)));
            }
        });
    }

    private void refresh(final String hxid, boolean backTask) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载资料...");
        progressDialog.setCanceledOnTouchOutside(false);
        if (!backTask) {
            progressDialog.show();
        }
        List<Param> parms = new ArrayList<>();
        parms.add(new Param("hxid", hxid));

        OkHttpManager.getInstance().post(parms, FXConstant.URL_Get_UserInfo, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                int code = jsonObject.getInteger("code");
                if (code == 1) {
                    JSONObject json = jsonObject.getJSONObject("user");
                    //刷新ui
                    initView(json);
                    if (DemoHelper.getInstance().getContactList().containsKey(hxid)) {
                        EaseUser user = JSONUtil.Json2User(json);
                        UserDao dao = new UserDao(UserDetailsActivity.this);
                        dao.saveContact(user);
                        DemoHelper.getInstance().getContactList().put(user.getUsername(), user);
                    }

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });


    }


}