package com.htmessage.yichat.acitivity.red.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.red.RedDetailActivity;

/**
 * 项目名称：hanxuan
 * 类描述：DialogOpenActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/22 17:51
 * 邮箱:814326663@qq.com
 */
public class DialogOpenActivity extends Activity implements View.OnClickListener {
    private ImageView iv_red_open, iv_red_close, iv_avatar;
    private TextView tv_nick, tv_red_from, tv_content;
    //,tv_look_others;
    private JSONObject redData;
    private ObjectAnimator objectAnimator;
    private String chatTo;
    private int chatType = MessageUtils.CHAT_SINGLE;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    JSONObject data = (JSONObject) msg.obj;
                    startActivity(new Intent(DialogOpenActivity.this, RedDetailActivity.class)
                            .putExtra("chatType", chatType)
                            .putExtra("chatTo", chatTo)
                            .putExtra("data", data.toJSONString())
                    );
                    finish();

                    ApiUtis.getInstance().postJSON(new JSONObject(), Constant.URL_BALANCE, new ApiUtis.HttpCallBack() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            String code=jsonObject.getString("code");
                            if("0".equals(code)){
                                JSONObject data=jsonObject.getJSONObject("data");
                                Double balance=data.getDouble("balance");
                                //本地缓存---
                                WalletUtils.getInstance().saveBalance(balance);

                            }
                        }

                        @Override
                        public void onFailure(int errorCode) {

                        }
                    });



                    break;
                case 1001:
                    int resId = msg.arg1;
                    Toast.makeText(DialogOpenActivity.this, resId, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.red_dialog_open_rp);
        getData();
        initView();
        setListener();
    }

    private void setListener() {
        objectAnimator = ObjectAnimator.ofFloat(iv_red_open, "RotationY", new float[]{0.0F, 180.0F});
        objectAnimator.setDuration(500L);
        objectAnimator.setRepeatCount(-1);
        iv_red_close.setOnClickListener(this);
        iv_red_open.setOnClickListener(this);
    }

    private void initView() {
        iv_avatar = (ImageView) findViewById(R.id.iv_header);
        iv_red_open = (ImageView) findViewById(R.id.iv_open_rp);
        iv_red_close = (ImageView) findViewById(R.id.iv_close);
        tv_nick = (TextView) findViewById(R.id.tv_name);
        tv_red_from = (TextView) findViewById(R.id.tv_send_rp);
        tv_content = (TextView) findViewById(R.id.tv_tip);
        // tv_look_others = (TextView) findViewById(R.id.tv_look_others);
        UserManager.get().loadUserAvatar(DialogOpenActivity.this, redData.getString("avatar"), iv_avatar);
        tv_nick.setText(redData.getString("nick"));
        tv_content.setText(redData.getString("content"));


    }

    private void getData() {
        chatTo = getIntent().getStringExtra("chatTo");
        chatType = getIntent().getIntExtra("chatType", MessageUtils.CHAT_SINGLE);
        redData = JSONObject.parseObject(getIntent().getStringExtra("data"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_open_rp:
                if (objectAnimator != null) {
                    objectAnimator.start();
                    openRed();
                }
                break;
//            case R.id.tv_look_others:
//
//
//                startActivity(new Intent(getBaseContext(), RedDetailActivity.class).putExtra("rpObj", redJson.toJSONString()).putExtra("envId", envId).putExtra("chatType", chatType).putExtra("message", message).putExtra("toUser", toUser));
//                finish();
//                break;
        }
    }


    private void openRed() {

        JSONObject body = new JSONObject();
        body.put("packetId", redData.getString("packetId"));
        ApiUtis.getInstance().postJSON(body, Constant.URL_RedPacket_Receive, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.sendToTarget();
                    LocalBroadcastManager.getInstance(DialogOpenActivity.this).sendBroadcast(new Intent(IMAction.RED_PACKET_HAS_GOT)
                            .putExtra("whiosRP",redData.getString("userId")).putExtra("msgId",redData.getString("msgId")));
                } else if ("225".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.receive_rp_error225;
                    message.sendToTarget();
                } else if ("222".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.receive_rp_error222;
                    message.sendToTarget();
                } else if ("223".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.receive_rp_error223;
                    message.sendToTarget();
                } else if ("224".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.receive_rp_error224;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.receive_rp_error221;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUserId()));
//        params.add(new Param("redPacketId", envId));
//
//        Log.d("toUser--->",toUser);
//        params.add(new Param("isGroupRed", isGroupRed));
//        String url=    HTConstant.GET_RP_SINGLE;
//        if(isGroupRed.equals("1")){
//            url= HTConstant.GET_RP;
//            params.add(new Param("gid", groupId));
//        }
//
//        new OkHttpUtils(getBaseContext()).post(params,url , new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e( "-----领取红包:" + jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        if (var1 != null) {
//                            var1.cancel();
//                        }
//                        HTMessageUtils.updateRpMessage(message, getBaseContext());
//                        startActivity(new Intent(getBaseContext(), RedDetailActivity.class).putExtra("rpObj", redJson.toJSONString()).putExtra("envId", envId).putExtra("chatType", chatType).putExtra("message", message).putExtra("toUser",toUser));
//                        finish();
//                        break;
//                    default:
//                        if (var1 != null) {
//                            var1.cancel();
//                            iv_red_open.setVisibility(View.INVISIBLE);
//                            tv_red_from.setVisibility(View.INVISIBLE);
//                            tv_content.setText(R.string.no_rp);
//                            tv_look_others.setVisibility(View.VISIBLE);
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                if (var1 != null) {
//                    var1.cancel();
//                    iv_red_open.setVisibility(View.INVISIBLE);
//                    tv_red_from.setVisibility(View.INVISIBLE);
//                    tv_content.setText(R.string.no_rp);
//                    tv_look_others.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }
}
