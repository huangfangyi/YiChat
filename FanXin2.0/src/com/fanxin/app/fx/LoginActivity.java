package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.UserDao;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
    private EditText et_usertel;
    private EditText et_password;
    private Button btn_login;
    private Button btn_qtlogin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_qtlogin = (Button) findViewById(R.id.btn_qtlogin);
        // 监听多个输入框

        et_usertel.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());

        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.setMessage("正在登录...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();

                final String password = et_password.getText().toString().trim();
                String usertel = et_usertel.getText().toString().trim();
                Map<String, String> map = new HashMap<String, String>();

                map.put("usertel", usertel);
                map.put("password", password);
                LoadDataFromServer task = new LoadDataFromServer(
                        LoginActivity.this, Constant.URL_Login, map);

                task.getData(new DataCallBack() {

                    @Override
                    public void onDataCallBack(JSONObject data) {

                        if (data == null) {
                            Toast.makeText(LoginActivity.this,
                                    "返回数据错误../", Toast.LENGTH_SHORT)
                                    .show();
                                return ;
                        }
                        try {
                            int code = data.getInteger("code");
                            if (code == 1) {

                                JSONObject json = data.getJSONObject("user");
                                login(json);
                            } else if (code == 2) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,
                                        "账号或密码错误...", Toast.LENGTH_SHORT)
                                        .show();
                            } else if (code == 3) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,
                                        "服务器端注册失败...", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,
                                        "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } catch (JSONException e) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "数据解析错误...",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
        btn_qtlogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
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
                btn_login.setTextColor(0xFFFFFFFF);
                btn_login.setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {
                btn_login.setTextColor(0xFFD0EFC6);
                btn_login.setEnabled(false);
            }
        }

    }

    private void login(final JSONObject json) {

        try {
            final String nick = json.getString("nick");
            final String hxid = json.getString("hxid");
            final String password = json.getString("password");
            // String fxid = json.getString("fxid");
            // String tel = json.getString("tel");
            // String sex = json.getString("sex");
            // String sign = json.getString("sign");
            // String avatar = json.getString("avatar");
            // String region = json.getString("region");
            // 调用sdk登陆方法登陆聊天服务器
            EMChatManager.getInstance().login(hxid, password, new EMCallBack() {

                @Override
                public void onSuccess() {

                    // 登陆成功，保存用户名密码
                    MYApplication.getInstance().setUserName(hxid);
                    MYApplication.getInstance().setPassword(password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            dialog.setMessage(getString(R.string.list_is_for));
                        }
                    });
                    try {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        // conversations in case we are auto login
                        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                        boolean updatenick = EMChatManager.getInstance()
                                .updateCurrentUserNick(nick);
                        if (!updatenick) {
                            Log.e("LoginActivity",
                                    "update current user nick fail");
                        }
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        // 处理好友和群组
                        runOnUiThread(new Runnable() {
                            public void run() {
                                processContactsAndGroups(json);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        // 取好友或者群聊失败，不让进入主页面
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                MYApplication.getInstance().logout(null);
                                Toast.makeText(getApplicationContext(),
                                        R.string.login_failure_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.Login_failed) + message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (JSONException e1) {

            e1.printStackTrace();
        }

    }

    private void processContactsAndGroups(final JSONObject json) {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定

        // try {
        // List<String> usernames = EMContactManager.getInstance()
        // .getContactUserNames();
        // if (usernames != null && usernames.size() > 0) {
        // String totaluser = usernames.get(0);
        // for (int i = 1; i < usernames.size(); i++) {
        // final String split = "66split88";
        // totaluser += split + usernames.get(i);
        // }
        // totaluser = totaluser
        // .replace(Constant.NEW_FRIENDS_USERNAME, "");
        // totaluser = totaluser.replace(Constant.GROUP_USERNAME, "");
        // Log.e("totaluser---->>>>>",totaluser);
        Map<String, String> map = new HashMap<String, String>();

        // map.put("uids", totaluser);
        map.put("hxid", MYApplication.getInstance().getUserName());
        LoadDataFromServer task = new LoadDataFromServer(LoginActivity.this,
                Constant.URL_FriendList, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1000) {
                        JSONArray josnArray = data.getJSONArray("friends");
                        // 己的信息
                        saveMyInfo(json);

                        saveFriends(josnArray);

                    }
                    // else if (code == 2) {
                    // dialog.dismiss();
                    // Toast.makeText(LoginActivity.this,
                    // "获取好友列表失败,请重试...", Toast.LENGTH_SHORT)
                    // .show();
                    // }
                    else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        // } else {
        // // 己的信息
        // saveMyInfo(json);
        //
        // saveFriends(null);
        // }
        // } catch (EaseMobException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    @SuppressLint("DefaultLocale")
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        headerName = headerName.trim();
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                    1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

    private void saveMyInfo(JSONObject json) {

        try {
            String hxid = json.getString("hxid");
            String fxid = json.getString("fxid");
            String nick = json.getString("nick");
            String avatar = json.getString("avatar");
            String password = json.getString("password");
            String sex = json.getString("sex");
            String region = json.getString("region");
            String sign = json.getString("sign");
            String tel = json.getString("tel");
            String money  = json.getString("money");
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo(
                    "password", password);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("hxid",
                    hxid);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("fxid",
                    fxid);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("nick",
                    nick);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("avatar",
                    avatar);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("sex",
                    sex);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("region",
                    region);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("sign",
                    sign);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("tel",
                    tel);
            LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("money",
                    money);
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            return;
        }

    }

    private void saveFriends(JSONArray josnArray) {

        Map<String, User> map = new HashMap<String, User>();

        if (josnArray != null) {
            for (int i = 0; i < josnArray.size(); i++) {
                JSONObject json = josnArray.getJSONObject(i);
                try {
                    String hxid = json.getString("hxid");
                    String fxid = json.getString("fxid");
                    String nick = json.getString("nick");
                    String avatar = json.getString("avatar");
                    String sex = json.getString("sex");
                    String region = json.getString("region");
                    String sign = json.getString("sign");
                    String tel = json.getString("tel");

                    User user = new User();
                    user.setFxid(fxid);
                    user.setUsername(hxid);
                    user.setBeizhu("");
                    user.setNick(nick);
                    user.setRegion(region);
                    user.setSex(sex);
                    user.setTel(tel);
                    user.setSign(sign);
                    user.setAvatar(avatar);
                    setUserHearder(hxid, user);
                    map.put(hxid, user);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);
        newFriends.setBeizhu("");
        newFriends.setFxid("");
        newFriends.setHeader("");
        newFriends.setRegion("");
        newFriends.setSex("");
        newFriends.setTel("");
        newFriends.setSign("");
        newFriends.setAvatar("");
        map.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        groupUser.setNick(strChat);
        groupUser.setBeizhu("");
        groupUser.setFxid("");
        groupUser.setHeader("");
        groupUser.setRegion("");
        groupUser.setSex("");
        groupUser.setTel("");
        groupUser.setSign("");
        groupUser.setAvatar("");
        map.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        MYApplication.getInstance().setContactList(map);
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(map.values());
        dao.saveContactList(users);

        // 获取黑名单列表

//        try {
//            List<String> blackList = EMContactManager.getInstance()
//                    .getBlackListUsernamesFromServer();
//            EMContactManager.getInstance().saveBlackList(blackList);

            // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
            try {
                EMGroupManager.getInstance().getGroupsFromServer();
            } catch (EaseMobException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
     //       addContact("11223354");
            if (!LoginActivity.this.isFinishing())
                dialog.dismiss();
            // 进入主页面
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        

    }

    /**
     * 添加contact
     * 
     * @param view
     */
    @SuppressLint("ShowToast")
    public void addContact(final String glufine_id) {
        // 11223354
        if (glufine_id == null || glufine_id.equals("")) {
            return;
        }

        if (MYApplication.getInstance().getUserName().equals(glufine_id)) {

            return;
        }

        if (MYApplication.getInstance().getContactList()
                .containsKey(glufine_id)) {

            return;
        }

        new Thread(new Runnable() {
            public void run() {

                try {
                    // 在reason封装请求者的昵称/头像/时间等信息，在通知中显示

                    String name = LocalUserInfo.getInstance(LoginActivity.this)
                            .getUserInfo("nick");
                    String avatar = LocalUserInfo.getInstance(
                            LoginActivity.this).getUserInfo("avatar");
                    long time = System.currentTimeMillis();

                    String reason = name + "66split88" + avatar + "66split88"
                            + String.valueOf(time) + "66split88" + "加你好友";
                    EMContactManager.getInstance().addContact(glufine_id,
                            reason);

                } catch (final Exception e) {

                }
            }
        }).start();
    }

}
