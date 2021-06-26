package com.htmessage.yichat.acitivity.red;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2019/6/3.
 * qq 84543217
 */
public class TransferDetailsActivity extends BaseActivity  implements View.OnClickListener {
    //一共有6总界面状态
    private int type = 0;//0--发送方-等待对方接收
    //1-发送方--对方已接收
    //2-发送方--对方已退回
    //3-接收方--领取界面
    //4-接收方--我已领取
    //5-接收方--我已退回
    //6-转账已过期
    private TextView tvType, tvMoney, tvNote, tvTime, tvBackTime,tvBack;
    private Button btnSure;
    private LinearLayout llBack;
    private ImageView ivType;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_transfer_details);
        String status=getIntent().getStringExtra("status");
        if(TextUtils.isEmpty(status)){
            finish();
            return;
        }
        String toUser=getIntent().getStringExtra("toUser");
       String temp=   getIntent().getStringExtra("data");
        jsonObject=JSONObject.parseObject(temp);
        if(toUser.equals(HTApp.getInstance().getUsername())){
            //我是接收方
            if(status.equals("0")){
                //我未领取
                type=3;

            }else if(status.equals("1")){
                //我已领取
                type=4;

            }else if(status.equals("2")){
                //已过期
                type=5;
            }else if(status.equals("3")){
                //已过期
                type=5;
            }

        }else {
            //我是发送方
            if(status.equals("0")){
                //对方未领取
                type=0;

            }else if(status.equals("1")){
                //对方已领取
                type=1;

            }else if(status.equals("2")){
                //已过期
                type=2;
            }else if(status.equals("3")){
                //已过期
                type=2;
            }
        }

        initView(toUser);




    }

    private void initView(String toUser) {

        tvTime = findViewById(R.id.tv_time_send);
        tvBackTime = findViewById(R.id.tv_time_back);
        tvMoney = findViewById(R.id.tv_money);
        tvNote = findViewById(R.id.tv_note);
        tvType = findViewById(R.id.tv_type);
        llBack = findViewById(R.id.ll_back);
        btnSure = findViewById(R.id.btn_sure);
        ivType = findViewById(R.id.iv_type);
        tvBack = findViewById(R.id.tv_back);
        switch (type) {
            case 0:
                btnSure.setVisibility(View.GONE);
                llBack.setVisibility(View.GONE);
                tvBackTime.setVisibility(View.GONE);
                ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_wait);

                    tvType.setText("待"+UserManager.get().getUserNick(toUser)+"收款");

                 break;
            case 1:

                btnSure.setVisibility(View.GONE);
                llBack.setVisibility(View.GONE);
                tvBackTime.setVisibility(View.GONE);
                ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_succ);

                String nick= UserManager.get().getUserNick(toUser);


                    tvType.setText(nick);

                tvNote.setVisibility(View.GONE);

                break;
            case 2:
                tvType.setText("已退款");
                ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_reback);
                tvNote.setText("已退款到钱包");

                break;
            case 3:
                 ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_wait);
                 tvType.setText("待确认收款");
                 llBack.setVisibility(View.VISIBLE);
                 btnSure.setOnClickListener(this);
                tvBack.setOnClickListener(this);


                break;
            case 4:

                btnSure.setVisibility(View.GONE);
                llBack.setVisibility(View.GONE);
                tvBackTime.setVisibility(View.GONE);
                ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_succ);
                    tvType.setText( "已收钱");
                    tvNote.setText("已存入，您可在钱包中查看");

                break;
            case 5:
                btnSure.setVisibility(View.GONE);
                llBack.setVisibility(View.GONE);
                tvBackTime.setVisibility(View.GONE);
                ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_reback);
                tvNote.setVisibility(View.GONE);
                tvType.setText("已退还");

                break;
            case 6:
                break;


        }
        tvTime.setText(DateUtils.getStringTime(Long.parseLong(jsonObject.getString("createTime"))*1000));
        tvMoney.setText(jsonObject.getString("money"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sure:
                getTransfer();
                break;
            case R.id.tv_back:
                rebackTransfer();
                break;
        }
    }


    private void getTransfer(){

        JSONObject body=new JSONObject();
        body.put("packetId",jsonObject.getString("redPacketId"));
        ApiUtis.getInstance().postJSON(body, Constant.URL_RedPacket_Receive, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){

                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        params.add(new Param("redPacketId", jsonObject.getString("redPacketId")));
//        params.add(new Param("isGroupRed", "2"));
//        String url=    HTConstant.GET_RP_SINGLE;
//
//        new OkHttpUtils(getBaseContext()).post(params,url , new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                 int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//
//                        btnSure.setVisibility(View.GONE);
//                        llBack.setVisibility(View.GONE);
//                        tvBackTime.setVisibility(View.GONE);
//                        ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_succ);
//                        tvType.setText( "已收钱");
//                        tvNote.setText("已存入，您可在钱包中查看");
//                        break;
//                    default:
//                        Toast.makeText(getApplicationContext(),"领取失败", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(getApplicationContext(),"领取失败", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    private void rebackTransfer(){

        List<Param> params = new ArrayList<>();

        params.add(new Param("id", jsonObject.getString("redPacketId")));

        String url=    HTConstant.REBACK_TRANSFER;

        new OkHttpUtils(getBaseContext()).post(params,url , new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:

                        btnSure.setVisibility(View.GONE);
                        llBack.setVisibility(View.GONE);
                        tvBackTime.setVisibility(View.GONE);
                        ivType.setImageResource(R.drawable.jrmf_rp_ic_trans_reback);
                        tvNote.setVisibility(View.GONE);
                        tvType.setText("已退还");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"接口调用失败", Toast.LENGTH_SHORT).show();

                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(getApplicationContext(),"接口调用失败", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
