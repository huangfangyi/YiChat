package com.htmessage.fanxinht.acitivity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.LoginActivity;
import com.htmessage.fanxinht.utils.CommonUtils;

/**
 * 项目名称：yichat0504
 * 类描述：AuthFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/17 17:12
 * 邮箱:814326663@qq.com
 */
public class AuthFragment extends Fragment implements AuthView, View.OnClickListener {
    private AuthPresenter presenter;
    private ImageView iv_appicon, iv_back;
    private Button btn_config_login;
    private TextView tv_appname;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!HTClient.getInstance().isLogined()) {
            getBaseActivity().startActivityForResult(new Intent(getBaseActivity(), LoginActivity.class).putExtra("isAuth", true), 2000);
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View authview = inflater.inflate(R.layout.activity_auth_open, container, false);
        initView(authview);
        setListener();
        return authview;
    }

    private void setListener() {
        btn_config_login.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    private void initView(View authview) {
        iv_back = (ImageView) authview.findViewById(R.id.iv_back);
        iv_appicon = (ImageView) authview.findViewById(R.id.iv_appicon);
        btn_config_login = (Button) authview.findViewById(R.id.btn_config_login);
        tv_appname = (TextView) authview.findViewById(R.id.tv_appname);
    }

    @Override
    public void showAppName(String appName) {
        tv_appname.setText(appName);
    }

    @Override
    public void showAppIcon(String appicon) {
        Glide.with(getBaseContext()).load(appicon).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(iv_appicon);
    }

    @Override
    public void authWebSuccess(String msg) {
        CommonUtils.showToastShort(getBaseActivity(), msg);
        getBaseActivity().finish();
    }

    @Override
    public void authWebFailed(String error) {
        CommonUtils.showToastShort(getBaseActivity(), error);
        getBaseActivity().finish();
    }

    @Override
    public void setPresenter(AuthPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                presenter.authResultError();
                break;
            case R.id.btn_config_login:
                presenter.authResultSuccess();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onDestroy() {
        presenter.onDestory();
        super.onDestroy();
    }
}
