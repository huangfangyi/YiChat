package com.htmessage.yichat.acitivity.details;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.acitivity.friends.addfriend.AddFriendsFinalActivity;
import com.htmessage.yichat.acitivity.moments.MomentsFriendActivity;
import com.htmessage.yichat.utils.CommonUtils;


/**
 * Created by huangfangyi on 2019/7/29.
 * qq 84543217
 */
public class UserDetailActivity extends BaseActivity {

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    initView(jsonObject);
                    break;
                case 1001:
                    int resId = msg.arg1;
                    Toast.makeText(UserDetailActivity.this, resId, Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
                    CommonUtils.cencelDialog();
                    String remark = (String) msg.obj;
                    tv_remark.setText(remark);

                    break;

            }
        }
    };

    private TextView tvNick;
    private TextView tvAppId;
    private ImageView ivGender;
    private ImageView ivAvatar;
    private RelativeLayout rlRemark;
    private TextView tv_remark;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_userinfo);
        tvNick = findViewById(R.id.tv_name);
        tvAppId = findViewById(R.id.tv_app_id);
        ivGender = findViewById(R.id.iv_gender);
        ivAvatar = findViewById(R.id.iv_avatar);
        rlRemark = findViewById(R.id.rl_remark);
        tv_remark = findViewById(R.id.tv_remark);
        String userString = getIntent().getStringExtra("data");
        if (userString != null) {
            JSONObject jsonObject = JSONObject.parseObject(userString);
            initView(jsonObject);
            return;
        }
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            tvNick.setText(UserManager.get().getUserNick(userId));
            UserManager.get().loadUserAvatar(UserDetailActivity.this,
                    UserManager.get().getUserAvatar(userId), ivAvatar);
            getUserInfo(userId);
        }


    }

    private String remark;

    private void initView(JSONObject jsonObject) {
        final String userId = jsonObject.getString("userId");
        String nick = jsonObject.getString("nick");
        String avatar = jsonObject.getString("avatar");
        String gender = jsonObject.getString("gender");
        String appId = jsonObject.getString("appId");
        String remarkServer = jsonObject.getString("remark");
        String friendStatus = jsonObject.getString("friendStatus");
        UserManager.get().loadUserAvatar(UserDetailActivity.this, avatar, ivAvatar);
        tvNick.setText(nick);
        tvAppId.setText(appId);
        if (TextUtils.isEmpty(gender) || "0".equals(gender)) {
            ivGender.setImageResource(R.drawable.icon_female);
        } else {
            ivGender.setImageResource(R.drawable.icon_male);

        }

        Button btnMsg = this.findViewById(R.id.btn_msg);
        Button btnAdd = this.findViewById(R.id.btn_add);

        if ("0".equals(friendStatus)) {
            btnMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserDetailActivity.this, ChatActivity.class).putExtra("userId", userId));
                }
            });

        } else if ("1".equals(friendStatus)) {

            btnMsg.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserDetailActivity.this, AddFriendsFinalActivity.class).putExtra("userId", userId));

                }
            });


        }

        if (UserManager.get().getFriends().contains(userId)) {
            //是好友的，显示备注
            rlRemark.setVisibility(View.VISIBLE);
            remark = UserManager.get().getUserRemark(userId);
            if (!TextUtils.isEmpty(remarkServer)) {
                tv_remark.setText(remarkServer);
            } else if (!TextUtils.isEmpty(remark)) {
                tv_remark.setText(remarkServer);

            } else {
                remark = UserManager.get().getUserNick(userId);
                ;
            }

            rlRemark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.showInputDialog(UserDetailActivity.this, "设置备注",


                            "备注", remark, new CommonUtils.DialogClickListener() {
                                @Override
                                public void onCancleClock() {

                                }

                                @Override
                                public void onPriformClock(String msg) {
                                    if (remark.equals(msg)) {
                                        return;
                                    }
                                    updateRemarkInServer(userId, msg);
                                }
                            });

                }
            });

            this.findViewById(R.id.rl_xiangce).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserDetailActivity.this, MomentsFriendActivity.class).putExtra("userId", userId));
                    finish();
                }
            });
        }

        if (UserManager.get().getMyUserId().equals(userId)) {
            rlRemark.setVisibility(View.GONE);

            btnMsg.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
        }
    }

    private void getUserInfo(String userId) {
        JSONObject data = new JSONObject();
        data.put("userId", userId);
        ApiUtis.getInstance().postJSON(data, Constant.URL_USER_INFO, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }

                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    UserManager.get().saveUserInfo(data);
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.sendToTarget();


                } else if ("116".equals(code)) {

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.user_not_exit;
                    message.sendToTarget();


//
//                    if(activity!=null&&!activity.isDestroyed()){
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(activity,R.string.user_not_exit,Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//                    }
                } else if ("004".equals(code)) {

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_10;
                    message.sendToTarget();


                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(final int errorCode) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

    }


    private void updateRemarkInServer(String userId, String remarkNew) {
//        if(TextUtils.isEmpty(remark)){
//            return;
//        }
        CommonUtils.showDialogNumal(UserDetailActivity.this, "");
        JSONObject body = new JSONObject();
        body.put("friendId", userId);
        body.put("remark", remarkNew);
        ApiUtis.getInstance().postJSON(body, Constant.URL_REMARK, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    UserDetailActivity.this.remark = remarkNew;
                    UserManager.get().setUserRemark(userId, remarkNew);
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = remarkNew;
                    message.sendToTarget();
                    Intent intent = new Intent(IMAction.USER_REMARK);
                    intent.putExtra("userId", userId);

                    LocalBroadcastManager.getInstance(UserDetailActivity.this).sendBroadcast(intent);
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
