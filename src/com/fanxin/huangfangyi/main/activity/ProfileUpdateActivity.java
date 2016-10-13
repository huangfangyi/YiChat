package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/3.\
 * QQ:84543217
 */
public class ProfileUpdateActivity extends BaseActivity {
    public static final int TYPE_NICK = 0;
    public static final int TYPE_FXID = 1;
    public static final int TYPE_SIGN = 2;

    private String defaultStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_update_info);
        int type = getIntent().getIntExtra("type", 0);
        defaultStr = getIntent().getStringExtra("default");
        TextView titleTV = (TextView) findViewById(R.id.tv_title);
        TextView saveTV = (TextView) findViewById(R.id.tv_save);
        EditText infoET = (EditText) findViewById(R.id.et_info);
        if (defaultStr != null) {

            infoET.setText(defaultStr);
        }
        initView(type, titleTV, saveTV, infoET);


    }

    private void initView(int type, TextView titleTV, TextView saveTV, final EditText infoET) {
        String title = "";
        String key = "";

        switch (type) {
            case TYPE_NICK:
                title = "修改昵称";
                key = FXConstant.JSON_KEY_NICK;
                break;
            case TYPE_FXID:
                title = "修改凡信号";
                key = FXConstant.JSON_KEY_FXID;
                break;
            case TYPE_SIGN:
                title = "修改个人签名";
                key = FXConstant.JSON_KEY_SIGN;
                break;

        }
        titleTV.setText(title);
        final String finalKey = key;
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateInServer(finalKey, infoET.getText().toString().trim());
            }
        });

    }

    private void updateInServer(final String key, final String value) {

        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || ((defaultStr != null) && value.equals(defaultStr))) {
            return;
        }
        if (value.length() > 30) {
            Toast.makeText(getApplicationContext(), "不能超过30个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在更新...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        //本地用户资料
        final JSONObject userJson = DemoApplication.getInstance().getUserJson();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("key", key));
        params.add(new Param("value", value));
        params.add(new Param("hxid", userJson.getString(FXConstant.JSON_KEY_HXID)));
        List<File> files = new ArrayList<File>();
        if (key == FXConstant.JSON_KEY_AVATAR) {
            File file = new File(PathUtil.getInstance().getImagePath(), value);
            if (file.exists()) {
                files.add(file);
            }
        }
        OkHttpManager.getInstance().post(params, files, FXConstant.URL_UPDATE, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    userJson.put(key, value);
                    DemoApplication.getInstance().setUserJson(userJson);
                    setResult(RESULT_OK, new Intent().putExtra("value", value));
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), "更新失败,code:" + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
            }
        });

    }

}
