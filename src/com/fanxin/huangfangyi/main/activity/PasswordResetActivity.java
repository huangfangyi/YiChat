package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/10/7.
 * qq 84543217
 */

public class PasswordResetActivity extends BaseActivity {
    private String password;
    private String confirePassword;
    private String mobile;
    private boolean isReset;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_psw_reset);
        isReset = this.getIntent().getBooleanExtra("isReset", false);
        initView();

    }

    private void initView() {
        final EditText et_usertel = (EditText) this.findViewById(R.id.et_usertel);
        final EditText et_password = (EditText) this.findViewById(R.id.et_password);
        final EditText et_password_confire = (EditText) this.findViewById(R.id.et_password_confire);
        btn_ok = (Button) this.findViewById(R.id.btn_ok);
        if (isReset) {
            et_usertel.setText(DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_TEL));
            et_usertel.setEnabled(false);
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = et_password.getText().toString().trim();
                confirePassword = et_password_confire.getText().toString().trim();
                mobile = et_usertel.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirePassword)) {
                    Toast.makeText(getApplicationContext(), "新密码不能为空", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!password.equals(confirePassword)) {
                    Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(getApplicationContext(), "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;

                }

                updatePasswordInServer();
            }
        });


    }


    private void updatePasswordInServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在重置密码...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("key", "s_password"));
        params.add(new Param("value", password));
        params.add(new Param("hxid", DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_HXID)));
        List<File> files = new ArrayList<File>();
        OkHttpManager.getInstance().post(params, files, FXConstant.URL_UPDATE, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    Toast.makeText(getApplicationContext(), "密码重置成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    Toast.makeText(getApplicationContext(), "密码重置失败,code:" + code, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
            }
        });
    }

}