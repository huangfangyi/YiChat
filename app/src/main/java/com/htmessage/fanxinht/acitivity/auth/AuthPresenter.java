package com.htmessage.fanxinht.acitivity.auth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：AuthPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/17 17:08
 * 邮箱:814326663@qq.com
 */
public class AuthPresenter implements AuthBasePresenter {
    private AuthView authView;
    private String appname;
    private String pagename;
    private String appicon;
    private String loginId;
    private boolean isWeb = false;


    public AuthPresenter(AuthView authView, Bundle bundle) {
        this.authView = authView;
        this.authView.setPresenter(this);
        appname = bundle.getString(HTConstant.JSON_KEY_THIRDAPPNAME);
        appicon = bundle.getString(HTConstant.JSON_KEY_THIRDAPPICON);
        isWeb = bundle.getBoolean(HTConstant.JSON_KEY_ISWEB, false);
        if (isWeb) {
            loginId = bundle.getString(HTConstant.JSON_KEY_LOGINID);
        } else {
            pagename = bundle.getString(HTConstant.JSON_KEY_PACKAGENAME);
        }
    }

    @Override
    public void OnActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 2000) {
            if (!isEmpty(appname) && !isEmpty(pagename)) {
                authView.showAppIcon(appicon);
                authView.showAppName(appname);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == 2000) {
            authResultError();
        }
    }

    @Override
    public void authResultError() {
        if (!isWeb) {
            authResult(null);
        } else {
            authWebResult(HTConstant.KEY_AUTH_FAILED);
        }
    }

    @Override
    public void authResultSuccess() {
        if (!isWeb) {
            authResult(HTApp.getInstance().getUserJson().toJSONString());
        } else {
            authWebResult(HTConstant.KEY_AUTH_SUCCESS);
        }
    }

    private void authWebResult(final String status) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("loginid", loginId));
        params.add(new Param("status", status));
        new OkHttpUtils(authView.getBaseActivity()).post(params, HTConstant.URL_AUTH_URL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String msg = null;
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        msg = authView.getBaseActivity().getString(R.string.auth_success);
                        break;
                    case 0:
                        msg = authView.getBaseActivity().getString(R.string.auth_failed);
                        break;
                    case -1:
                        msg = authView.getBaseActivity().getString(R.string.auth_failed);
                        break;
                }
                if (!HTConstant.KEY_AUTH_FAILED.equals(status)) {
                    authView.authWebSuccess(msg);
                } else {
                    authView.getBaseActivity().finish();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                authView.authWebFailed(errorMsg);
            }
        });
    }


    private void authResult(String userjson) {
        ComponentName apk2Component = new ComponentName(pagename, pagename + ".ycapi.YCApiActivity");
//................................................... 创建意图与参数对象
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
//................................................... 传递给apk2的参数
        bundle.putString("obj", userjson);//以obj为key把用户信息传过去
//................................................... 将参数对象与apk2组件设置给意图
        intent.setComponent(apk2Component);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        authView.getBaseActivity().startActivity(intent);
        authView.getBaseActivity().finish();
    }

    @Override
    public void onDestory() {
        authView = null;
    }

    @Override
    public void start() {
        authView.showAppName(appname);
        authView.showAppIcon(appicon);
    }

    private boolean isEmpty(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return true;
        }
        return false;
    }
}
