package com.htmessage.fanxinht.acitivity.chat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.acitivity.chat.group.GroupAddMembersActivity;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.sdk.client.HTClient;


public class ChatSettingSingleActivity extends BaseActivity implements
        OnClickListener {
    // 、置顶
    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    // 状态变化
    private ImageView iv_switch_chattotop;
    private ImageView iv_switch_unchattotop;
    private ImageView iv_switch_block_groupmsg;
    private ImageView iv_switch_unblock_groupmsg;

    private String userId;
    private Dialog progressDialog;
    public static ChatSettingSingleActivity instance;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting_single);
        instance = this;
        // 获取传过来的userId
        userId = getIntent().getStringExtra("userId");
        user= ContactsManager.getInstance().getContactList().get(userId);
        // 资料错误则不显示
        if (user == null) {
            finish();
            return;
        }
        initView();
        initData();

    }

    private void initView() {

        rl_switch_chattotop = (RelativeLayout) findViewById(R.id.rl_switch_chattotop);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);

        iv_switch_chattotop = (ImageView) findViewById(R.id.iv_switch_chattotop);
        iv_switch_unchattotop = (ImageView) findViewById(R.id.iv_switch_unchattotop);
        iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);

//        // 初始化置顶和免打扰的状态
//        if (!blackList.contains(userId)) {
//            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
//            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
//
//        } else {
//            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
//            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
//        }


    }

    private void initData() {

        rl_switch_chattotop.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        re_clear.setOnClickListener(this);

        ImageView ivAvatar= (ImageView) this.findViewById(R.id.iv_avatar);
        TextView tvNick = (TextView) this.findViewById(R.id.tv_username);
        tvNick.setText(user.getNick());
        String avatarUrl =user.getAvatar();
//        String avatarUrl = HTConstant.URL_AVATAR + userJson.getString(HTConstant.JSON_KEY_AVATAR);
        if(!TextUtils.isEmpty(avatarUrl)){
            if (!avatarUrl.contains("http:")){
                avatarUrl = HTConstant.URL_AVATAR+avatarUrl;
            }
        }
        Glide.with(this).load( avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);

        ivAvatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatSettingSingleActivity.this,
                        UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO, user.getUserInfo()));
            }
        });
        ImageView ivAdd = (ImageView) this.findViewById(R.id.iv_avatar2);
        ivAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatSettingSingleActivity.this,
                        GroupAddMembersActivity.class).putExtra("userId", userId));

            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_switch_block_groupmsg: // 设置免打扰
            progressDialog = HTApp.getInstance().createLoadingDialog(this,"正在设置免打扰...");
            progressDialog.show();
            if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        removeOutBlacklist(userId);
                        progressDialog.dismiss();

                    }

                }, 2000);

            } else {
                moveToBlacklist(userId);
            }
            break;

        case R.id.re_clear: // 清空聊天记录
            progressDialog = HTApp.getInstance().createLoadingDialog(this,"正在清空聊天记录...");
            progressDialog.show();
             new Handler().postDelayed(new Runnable() {

                public void run() {
                    HTClient.getInstance().conversationManager().deleteConversationAndMessage(userId);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_EMPTY).putExtra("id", userId));
                    progressDialog.dismiss();

                }

            }, 2000);

            break;

        case R.id.rl_switch_chattotop:
//            // 当前状态是已经置顶,点击后取消置顶
//            if (iv_switch_chattotop.getVisibility() == View.VISIBLE) {
//
//                iv_switch_chattotop.setVisibility(View.INVISIBLE);
//                iv_switch_unchattotop.setVisibility(View.VISIBLE);
//
//                if (topMap.containsKey(userId)) {
//
//                    topMap.remove(userId);
//                    TopUserDao topUserDao = new TopUserDao(
//                            ChatSettingSingleActivity.this);
//
//                    topUserDao.deleteTopUser(userId);
//
//                }
//
//            } else {
//
//                // 当前状态是未置顶点击后置顶
//
//                iv_switch_chattotop.setVisibility(View.VISIBLE);
//                iv_switch_unchattotop.setVisibility(View.INVISIBLE);
//
//                if (!topMap.containsKey(userId)) {
//                    TopUser topUser = new TopUser();
//                    topUser.setTime(System.currentTimeMillis());
//                    // 1---表示是群组0----个人
//                    topUser.setType(0);
//                    topUser.setUserName(userId);
//                    Map<String, TopUser> map = new HashMap<String, TopUser>();
//                    map.put(userId, topUser);
//                    topMap.putAll(map);
//                    TopUserDao topUserDao = new TopUserDao(
//                            ChatSettingSingleActivity.this);
//                    topUserDao.saveTopUser(topUser);
//
//                }
//
//            }

            break;

        default:
            break;
        }

    }


    /**
     * 把user移入到免打扰
     */
    private void moveToBlacklist(final String username) {
//
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    // 加入到黑名单
//                    EMClient.getInstance().contactManager().addUserToBlackList(username,
//                            false);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            iv_switch_block_groupmsg
//                                    .setVisibility(View.VISIBLE);
//                            iv_switch_unblock_groupmsg
//                                    .setVisibility(View.INVISIBLE);
//
//                        }
//                    });
//                }  catch (final HyphenateException e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(),
//                                    "设置失败，原因：" + e.toString(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    /**
     * 移出免打扰
     * 
     * @param tobeRemoveUser
     */
    private void removeOutBlacklist(final String tobeRemoveUser) {

//        try {
//
//            // 移出黑民单
//            EMClient.getInstance().contactManager().removeUserFromBlackList(tobeRemoveUser);
//            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
//            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
//        }   catch (HyphenateException e) {
//            runOnUiThread(new Runnable() {
//                public void run() {
//
//                    Toast.makeText(getApplicationContext(), "设置失败",
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//            e.printStackTrace();
//        }
    }


}
