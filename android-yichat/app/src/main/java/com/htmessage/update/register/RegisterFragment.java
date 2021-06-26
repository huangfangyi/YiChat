package com.htmessage.update.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.update.Constant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.main.WebViewActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.update.data.UserManager;

/**
 * Created by huangfangyi on 2017/6/23.
 * qq 84543217
 */

public class RegisterFragment extends Fragment implements View.OnClickListener, RegisterContract.View {

    private EditText et_usernick, et_password;
    private Button btn_register;
    private ImageView iv_hide, iv_show, iv_photo;
    private TextView tv_xieyi;

    private RegisterContract.Presenter mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        setLisenter();
    }


    private void initData() {
        String xieyi = "<font color=" + "\"" + "#AAAAAA" + "\">" + getString(R.string.press_top)
                + "&nbsp;" + "\"" + getString(R.string.register) + "\"" + "&nbsp;" + getString(R.string.btn_means_agree) + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + getString(R.string.Secret_agreement)
                + "</font>" + "</u>";
        tv_xieyi.setText(Html.fromHtml(xieyi));
        tv_xieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", Constant.XIEYI_REGISTER).putExtra("title","注册协议"));
            }
        });
    }

    private void initView() {
        et_usernick = (EditText) getView().findViewById(R.id.et_usernick);
        et_password = (EditText) getView().findViewById(R.id.et_password);
        btn_register = (Button) getView().findViewById(R.id.btn_register);
        tv_xieyi = (TextView) getView().findViewById(R.id.tv_xieyi);
        iv_hide = (ImageView) getView().findViewById(R.id.iv_hide);
        iv_show = (ImageView) getView().findViewById(R.id.iv_show);
        iv_photo = (ImageView) getView().findViewById(R.id.iv_photo);
     }

    private void setLisenter() {
        // 监听多个输入框
        TextChange textChange = new TextChange();
        et_usernick.addTextChangedListener(textChange);
        et_password.addTextChangedListener(textChange);
        iv_hide.setOnClickListener(this);
        iv_show.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        btn_register.setOnClickListener(this);

    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public void showAvatar(String imagePath) {

        UserManager.get().loadUserAvatar(getActivity(), imagePath, iv_photo);
    }

    @Override
    public void showDialog() {
        CommonUtils.showDialogNumal(getActivity(), getString(R.string.Is_the_registered));
    }

    @Override
    public void cancelDialog() {
        CommonUtils.cencelDialog();
    }

    @Override
    public void showPassword() {
        iv_show.setVisibility(View.GONE);
        iv_hide.setVisibility(View.VISIBLE);
      //  et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    // 切换后将密码EditText光标置于末尾
    private void editTextEnd() {
        CharSequence charSequence = et_password.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    @Override
    public void hidePassword() {
        iv_hide.setVisibility(View.GONE);
        iv_show.setVisibility(View.VISIBLE);
      //  et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    @Override
    public void enableButton() {
        btn_register.setEnabled(true);
    }

    @Override
    public void disableButton() {
        btn_register.setEnabled(false);
    }

    @Override
    public void showToast(Object msgRes) {
        CommonUtils.showToastShort(getActivity(), msgRes);
    }




    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void onRegisterSucc() {

        getActivity().finish();

    }


    // EditText监听器
    class TextChange implements TextWatcher {

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

            boolean sign1 = et_usernick.getText().length() > 0;
            boolean sign3 = et_password.getText().length() > 0;

            if (sign1 & sign3) {

                enableButton();
            } else {
                disableButton();
            }
        }

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hide:
                hidePassword();
                editTextEnd();
                break;
            case R.id.iv_show:
                showPassword();
                editTextEnd();
                break;
            case R.id.btn_register:
                String usernick = et_usernick.getText().toString().trim();
                String password = et_password.getText().toString().trim();


                if (TextUtils.isEmpty(password)) {
                    showToast(R.string.pwd_is_not_allow_null);
                    return;
                }
                if (TextUtils.isEmpty(usernick)) {
                    showToast(R.string.input_nick);
                    return;
                }
                if (usernick.length() > 10) {
                    showToast(R.string.string_not_10);
                    return;
                }
                if (!Validator.isPassword(password)) {
                    showToast(R.string.pwd_tips);
                    return;
                }
                String usertel = getBaseActivity().getIntent().getStringExtra("tel");
                mPresenter.registerInServer(usernick, usertel, password);
                break;

            case R.id.iv_photo:
                mPresenter.selectAvatar();
                break;


        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }


}



