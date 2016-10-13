package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.Constant;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.db.DemoDBManager;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.fragment.MainActivity;
import com.fanxin.huangfangyi.main.utils.JSONUtil;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private EditText et_usertel;
    private EditText et_password;
    private boolean autoLogin = false;
    private Button btn_login;
    private Button btn_qtlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }
        setContentView(R.layout.fx_activity_login);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_qtlogin = (Button) findViewById(R.id.btn_qtlogin);
        // 监听多个输入框
        TextChange textChange = new TextChange();
        et_usertel.addTextChangedListener(textChange);
        et_password.addTextChangedListener(textChange);
        // if user changed, clear the password
        et_usertel.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //TODO 此处可预置上次登陆的手机号
        //		if (DemoHelper.getInstance().getCurrentUsernName() != null) {
        //			et_usertel.setText(DemoHelper.getInstance().getCurrentUsernName());
        //		}


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInSever(et_usertel.getText().toString(), et_password.getText().toString());
            }
        });

        btn_qtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

//        this.findViewById(R.id.tv_wenti).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
//            }
//        });
    }

    private void loginInSever(String tel, String password) {
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("usertel", tel));
        params.add(new Param("password", password));
        OkHttpManager.getInstance().post(params, FXConstant.URL_LOGIN, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONObject json = jsonObject.getJSONObject("user");
                     saveFriends(json);

                    loginHuanXin(json, pd);
                } else if (code == 2001) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,
                            "账号或密码错误...", Toast.LENGTH_SHORT)
                            .show();
                }else {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,
                            "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }


    private void loginHuanXin(final JSONObject jsonObject, final ProgressDialog progressDialog) {
        final String nick = jsonObject.getString("nick");
        final String hxid = jsonObject.getString("hxid");
        final String password = jsonObject.getString("password");
        DemoDBManager.getInstance().closeDB();
        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(hxid);
         // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(hxid, password, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                if (!LoginActivity.this.isFinishing() && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // uprogressDialogate current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        nick);
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                // get user's info (this should be get from App's server or 3rd party service)
                // DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                jsonObject.remove("friends");
                DemoApplication.getInstance().setUserJson(jsonObject);
                // enter main activity
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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

            boolean Sign2 = et_usertel.getText().length() > 0;
            boolean Sign3 = et_password.getText().length() > 0;

            if (Sign2 & Sign3) {
                btn_login.setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {
                btn_login.setEnabled(false);
            }
        }

    }

    private void saveFriends(final JSONObject jsonObject){
       new Thread(new Runnable() {
           @Override
           public void run() {
                JSONArray friends=jsonObject.getJSONArray("friends");
               Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
               if (friends != null) {
                   for (int i = 0; i < friends.size(); i++) {
                       JSONObject friend = friends.getJSONObject(i);
                       EaseUser easeUser = JSONUtil.Json2User(friend);
                       userlist.put(easeUser.getUsername(), easeUser);
                   }
                   // save the contact list to cache
                   DemoHelper.getInstance().getContactList().clear();
                   DemoHelper.getInstance().getContactList().putAll(userlist);
                   // save the contact list to database
                   UserDao dao = new UserDao(getApplicationContext());
                   List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                   dao.saveContactList(users);

               }
               sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));

           }
       }).start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}
