package com.htmessage.fanxinht.acitivity.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.main.password.PasswordResetActivity;
import com.htmessage.fanxinht.acitivity.register.RegisterActivity;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.Validator;

/**
 * Created by dell on 2017/6/21.
 */

public class LoginFragment extends Fragment implements LoginContract.View, View.OnClickListener {
    private LoginContract.Presenter mPresenter;
    private EditText et_usertel, et_password;
    private TextView tv_find_password, tv_country, tv_country_code;
    private Button btn_login, btn_qtlogin;
    private RelativeLayout rl_country;
    private Dialog dialog;
    private boolean isAuth = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = HTApp.getInstance().createLoadingDialog(getActivity(), getString(R.string.logining));
        isAuth = getActivity().getIntent().getBooleanExtra("isAuth",false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        et_usertel = (EditText) root.findViewById(R.id.et_usertel);
        et_password = (EditText) root.findViewById(R.id.et_password);
        btn_login = (Button) root.findViewById(R.id.btn_login);
        tv_country = (TextView) root.findViewById(R.id.tv_country);
        tv_country_code = (TextView) root.findViewById(R.id.tv_country_code);
        tv_find_password = (TextView) root.findViewById(R.id.tv_find_password);
        rl_country = (RelativeLayout) root.findViewById(R.id.rl_country);
        btn_qtlogin = (Button) root.findViewById(R.id.btn_qtlogin);
        setLisenter();
        return root;
    }

    private void setLisenter() {
        //输入监听
        TextChange textChange = new TextChange();
        et_usertel.addTextChangedListener(textChange);
        et_password.addTextChangedListener(textChange);
        //登陆按钮监听
        btn_login.setOnClickListener(this);
        //选取国家监听
        rl_country.setOnClickListener(this);
        //跳转注册监听
        btn_qtlogin.setOnClickListener(this);
        tv_find_password.setOnClickListener(this);
    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }



    @Override
    public void showDialog() {
        if (dialog != null)
            dialog.show();
    }

    @Override
    public void cancelDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public String getUsername() {
        return et_usertel.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return et_password.getText().toString().trim();
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
        CommonUtils.showToastShort(getActivity(), toastMsg);
    }





    @Override
    public String getCountryName() {
        return tv_country.getText().toString().trim();
    }

    @Override
    public String getCountryCode() {
        return tv_country_code.getText().toString().trim();

    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (TextUtils.isEmpty(getUsername())) {
                    showToast( R.string.tel_is_not_allow_null );
                    return;
                }
                //如果是中国地区,进行一个手机号验证
                if (getCountryName().equals(getString(R.string.china)) && getCountryCode().equals(getString(R.string.country_code))) {
                    if (!Validator.isMobile(getUsername())) {
                        showToast( R.string.please_input_true_mobile );
                        return;
                    }
                }
                if (TextUtils.isEmpty(getPassword())) {
                    showToast( R.string.pwd_is_not_allow_null);
                    return;
                }
                mPresenter.requestServer(getUsername(), getPassword(),isAuth);
                break;
            case R.id.rl_country:
                mPresenter.chooseCuntry(getContext(), tv_country, tv_country_code);
                break;

            case R.id.btn_qtlogin:
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                break;
            case R.id.tv_find_password:
                startActivity(new Intent(getActivity(), PasswordResetActivity.class).putExtra("isReset", false));
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
            boolean sign1 = et_usertel.getText().length() > 0;
            boolean sign2 = et_password.getText().length() > 0;
            if (sign1 & sign2) {
                setButtonEnable();
            } else {
                setButtonDisabel();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
}
