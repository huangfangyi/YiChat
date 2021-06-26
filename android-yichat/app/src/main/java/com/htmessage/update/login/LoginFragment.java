package com.htmessage.update.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.main.MainActivity;
import com.htmessage.yichat.acitivity.password.PasswordResetActivity;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.register.PreRegisterActivity;
import com.htmessage.update.uitls.DialogUtils;
import com.htmessage.update.uitls.WidgetUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by dell on 2017/6/21.
 */

public class LoginFragment extends Fragment implements LoginContract.View, View.OnClickListener {
    private LoginContract.Presenter mPresenter;
    private EditText etMobile, etPassword;
    private TextView tv_find_password;
    private Button btn_login;
    private ImageView iv_qq_login, iv_wx_login;
    private WxLoginBrocast wxLoginBrocast;
    private Dialog dialog;
    private Tencent mTencent;
    public QQLoginWatcher loginWatcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getActivity());
        loginWatcher = new QQLoginWatcher();
        initView();
        setLisenter();
        wxLoginBrocast = new WxLoginBrocast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IMAction.LOGIN_BY_WECHAT_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(wxLoginBrocast, filter);
        //登录页，如果不调用微信登录，一定会调用输入法输入手机号，因此可在此处计算输入法高度
        int keyBoardHeight = SettingsManager.getInstance().getKeyboardHeight();
        if (keyBoardHeight == 0) {
            WidgetUtils.getKeyboardHeight(getActivity(), null);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            int type = bundle.getInt("type", 0);
            if (type == 1) {
                showConflicDialog();
            }
        }

    }
    public void showDialog(String msg){
        dialog = DialogUtils.creatDialog(getActivity(), R.string.logining);
        dialog.show();
    };

    private class QQLoginWatcher implements IUiListener {

        @Override
        public void onComplete(Object o) {
            if (o == null) {
                if(getActivity()!=null){
                    cancelDialog();
                    showToast(R.string.api_error_20);
                }
                return;
            }
              JSONObject object = JSONObject.parseObject(o.toString());
            String token = object.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = object.getString(Constants.PARAM_EXPIRES_IN);
            String openId = object.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
            UserInfo mInfo = new UserInfo(getActivity(), mTencent.getQQToken());
            mInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(final Object o) {
                    if (o == null) {
                        if(getActivity()!=null){
                            cancelDialog();
                            showToast(R.string.api_error_20);
                        }
                        return;
                    }
                     JSONObject jsonObject = JSONObject.parseObject(o.toString());
                    String openId1 = mTencent.getOpenId();
                    if(jsonObject!=null){
                        mPresenter.loginByQQ(token,openId,jsonObject.getString("nickname"),jsonObject.getString("figureurl_qq_2"));
                    }


                }

                @Override
                public void onError(UiError uiError) {
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cancelDialog();
                                    Toast.makeText(getActivity(),"QQ登录失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                }

                @Override
                public void onCancel() {
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelDialog();
                                Toast.makeText(getActivity(),"QQ登录取消",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            });

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }

    }

    private void showConflicDialog() {
        String st = getResources().getString(R.string.Logoff_notification);

        // clear up global variables
        try {

            AlertDialog.Builder exceptionBuilder = new AlertDialog.Builder(getActivity());

            exceptionBuilder.setTitle(st);
            exceptionBuilder.setMessage(R.string.connect_conflict);
            exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();


                }
            });
            exceptionBuilder.setCancelable(false);
            exceptionBuilder.show();
        } catch (Exception e) {
        }

    }


    private void initView() {
        etMobile = (EditText) getView().findViewById(R.id.et_usertel);
        etPassword = (EditText) getView().findViewById(R.id.et_password);
        btn_login = (Button) getView().findViewById(R.id.btn_login);
        tv_find_password = (TextView) getView().findViewById(R.id.tv_find_password);
        iv_qq_login = (ImageView) getView().findViewById(R.id.iv_qq_login);
        iv_wx_login = (ImageView) getView().findViewById(R.id.iv_wx_login);
    }

    private void setLisenter() {
        //输入监听
        TextChange textChange = new TextChange();
        etMobile.addTextChangedListener(textChange);
        etPassword.addTextChangedListener(textChange);
        btn_login.setOnClickListener(this);
        tv_find_password.setOnClickListener(this);
        iv_qq_login.setOnClickListener(this);
        iv_wx_login.setOnClickListener(this);

    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return null;
    }

    @Override
    public Activity getBaseActivity() {
        return null;
    }


    @Override
    public void cancelDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Override
    public void setButtonEnable() {
        btn_login.setEnabled(true);

    }

    @Override
    public void setButtonDisabel() {
        btn_login.setEnabled(true);

    }

    @Override
    public void showToast(int toastMsg) {
        Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccessed() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String mobile = etMobile.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast(R.string.tel_is_not_allow_null);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showToast(R.string.pwd_is_not_allow_null);
                    return;
                }
                dialog = DialogUtils.creatDialog(getActivity(), R.string.logining);
                dialog.show();
                mPresenter.requestServer(mobile, password);
                break;

            case R.id.tv_register:
                startActivity(new Intent(getActivity(), PreRegisterActivity.class));
                break;
            case R.id.tv_find_password:
                startActivity(new Intent(getActivity(), PasswordResetActivity.class).putExtra("isReset", false));
                break;
            case R.id.iv_qq_login:

                if (mTencent != null) {
                    if (!mTencent.isSessionValid()) {
                        mTencent.login(getActivity(), "all", loginWatcher);
                    } else {
                        mTencent.logout(getActivity());
                    }
                }


                // mPresenter.showThirdLoginDialog(getString(R.string.qq_login), HTConstant.qqType);
                break;
            case R.id.iv_wx_login:
                IWXAPI wxapi = WXAPIFactory.createWXAPI(getActivity(), HTConstant.WX_APP_ID_LOGIN);
                wxapi.registerApp(HTConstant.WX_APP_ID_LOGIN);
                if (!wxapi.isWXAppInstalled()) {
                    //提醒用户没有安装微信
                    showToast(R.string.has_no_wechat);
                    return;
                }
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = getString(R.string.app_name);
                wxapi.sendReq(req);

                // mPresenter.showThirdLoginDialog(getString(R.string.wx_login), HTConstant.wxType);
                break;


        }
    }


    // EditText监听器
    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean sign1 = etMobile.getText().length() > 0;
            boolean sign2 = etPassword.getText().length() > 0;
            if (sign1 & sign2) {
                setButtonEnable();
            } else {
                setButtonDisabel();
            }

        }
    }





    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(wxLoginBrocast);
        }
        super.onDestroy();
    }


    class WxLoginBrocast extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (IMAction.LOGIN_BY_WECHAT_RESULT.equals(intent.getAction())) {
                String resultCode = intent.getStringExtra("WX_RESULT_CODE");
                dialog = DialogUtils.creatDialog(getActivity(), R.string.logining);
                dialog.show();
                mPresenter.getWxToken(resultCode);
            }
        }
    }
}
