package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import java.util.ArrayList;
import java.util.List;


public class AddFriendsNextActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_addfriends_next);
        final RelativeLayout re_search = (RelativeLayout) this
                .findViewById(R.id.re_search);
        final TextView tv_search = (TextView) re_search
                .findViewById(R.id.tv_search);
        final EditText et_search = (EditText) this.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > 0) {
                    re_search.setVisibility(View.VISIBLE);
                    tv_search.setText(et_search.getText().toString().trim());
                } else {

                    re_search.setVisibility(View.GONE);
                    tv_search.setText("");

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        re_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String uid = et_search.getText().toString().trim();
                if (uid == null || uid.equals("")) {
                    return;
                }
                searchUser(uid);

            }

        });

    }

    private void searchUser(String uid) {
        final ProgressDialog dialog = new ProgressDialog(
                AddFriendsNextActivity.this);
        dialog.setMessage("正在查找联系人...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        List<Param> paramList=new ArrayList<>();
        paramList.add(new Param("uid", uid));
        OkHttpManager.getInstance().post(paramList, FXConstant.URL_Search_User, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                int code = jsonObject.getInteger("code");
                if (code == 1) {
                    JSONObject json = jsonObject.getJSONObject("user");
                    Intent intent = new Intent();

                    intent.setClass(AddFriendsNextActivity.this,
                            UserDetailsActivity.class).putExtra(FXConstant.KEY_USER_INFO,json.toJSONString());
                    startActivity(intent);
                } else if (code == 2) {

                    Toast.makeText(AddFriendsNextActivity.this, "用户不存在",
                            Toast.LENGTH_SHORT).show();
                } else if (code == 3) {

                    Toast.makeText(AddFriendsNextActivity.this,
                            "服务器查询错误...", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(AddFriendsNextActivity.this,
                            "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });



    }


}