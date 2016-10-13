package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.db.TopUser;
import com.fanxin.huangfangyi.main.db.TopUserDao;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatSettingSingleActivity extends BaseActivity implements
        OnClickListener {
    // 、置顶、、、、
    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    // 状态变化
    private ImageView iv_switch_chattotop;
    private ImageView iv_switch_unchattotop;
    private ImageView iv_switch_block_groupmsg;
    private ImageView iv_switch_unblock_groupmsg;

    private String userId;
    private List<String> blackList;
    // 置顶列表
    private  Map<String, TopUser> topMap = new HashMap<String, TopUser>();
    private ProgressDialog progressDialog;
    public static ChatSettingSingleActivity instance;
    private EaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_chat_setting_single);

        instance = this;
        // 获取传过来的userId
        userId = getIntent().getStringExtra("userId");
        user= DemoHelper.getInstance().getContactList().get(userId);
        // 资料错误则不显示
        if (user == null) {
            finish();
            return;
        }

        // 黑名单列表
        blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
        // 置顶列表
        topMap = DemoApplication.getInstance().getTopUserList();
        //
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
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

        // 初始化置顶和免打扰的状态
        if (!blackList.contains(userId)) {
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);

        } else {

            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);

        }
        if (!topMap.containsKey(userId)) {
            // 当前状态是w未置顶
            iv_switch_chattotop.setVisibility(View.INVISIBLE);
            iv_switch_unchattotop.setVisibility(View.VISIBLE);
        } else {
            // 当前状态是置顶
            iv_switch_chattotop.setVisibility(View.VISIBLE);
            iv_switch_unchattotop.setVisibility(View.INVISIBLE);
        }

    }

    private void initData() {

        rl_switch_chattotop.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        re_clear.setOnClickListener(this);

        ImageView ivAvatar= (ImageView) this.findViewById(R.id.iv_avatar);
        TextView tvNick = (TextView) this.findViewById(R.id.tv_username);
        tvNick.setText(user.getNick());
        Glide.with(this).load(FXConstant.URL_AVATAR+user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(ivAvatar);

        ivAvatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatSettingSingleActivity.this,
                        UserDetailsActivity.class).putExtra(FXConstant.KEY_USER_INFO, user.getUserInfo())
                       );

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
            progressDialog.setMessage("正在设置免打扰...");
            progressDialog.setCanceledOnTouchOutside(false);
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
            progressDialog.setMessage("正在清空消息...");
            progressDialog.show();
            // 按照你们要求必须有个提示，防止记录太少，删得太快，不提示
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    EMClient.getInstance().chatManager().deleteConversation(userId, true);
                    progressDialog.dismiss();

                }

            }, 2000);

            break;

        case R.id.rl_switch_chattotop:
            // 当前状态是已经置顶,点击后取消置顶
            if (iv_switch_chattotop.getVisibility() == View.VISIBLE) {

                iv_switch_chattotop.setVisibility(View.INVISIBLE);
                iv_switch_unchattotop.setVisibility(View.VISIBLE);

                if (topMap.containsKey(userId)) {

                    topMap.remove(userId);
                    TopUserDao topUserDao = new TopUserDao(
                            ChatSettingSingleActivity.this);

                    topUserDao.deleteTopUser(userId);

                }

            } else {

                // 当前状态是未置顶点击后置顶

                iv_switch_chattotop.setVisibility(View.VISIBLE);
                iv_switch_unchattotop.setVisibility(View.INVISIBLE);

                if (!topMap.containsKey(userId)) {
                    TopUser topUser = new TopUser();
                    topUser.setTime(System.currentTimeMillis());
                    // 1---表示是群组0----个人
                    topUser.setType(0);
                    topUser.setUserName(userId);
                    Map<String, TopUser> map = new HashMap<String, TopUser>();
                    map.put(userId, topUser);
                    topMap.putAll(map);
                    TopUserDao topUserDao = new TopUserDao(
                            ChatSettingSingleActivity.this);
                    topUserDao.saveTopUser(topUser);

                }

            }

            break;

        default:
            break;
        }

    }


    /**
     * 把user移入到免打扰
     */
    private void moveToBlacklist(final String username) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 加入到黑名单
                    EMClient.getInstance().contactManager().addUserToBlackList(username,
                            false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            iv_switch_block_groupmsg
                                    .setVisibility(View.VISIBLE);
                            iv_switch_unblock_groupmsg
                                    .setVisibility(View.INVISIBLE);

                        }
                    });
                }  catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "设置失败，原因：" + e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 移出免打扰
     * 
     * @param tobeRemoveUser
     */
    private void removeOutBlacklist(final String tobeRemoveUser) {

        try {

            // 移出黑民单
            EMClient.getInstance().contactManager().removeUserFromBlackList(tobeRemoveUser);
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
        }   catch (HyphenateException e) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(getApplicationContext(), "设置失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }


}
