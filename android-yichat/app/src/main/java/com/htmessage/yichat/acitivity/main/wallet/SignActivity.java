package com.htmessage.yichat.acitivity.main.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;

/**
 * Created by huangfangyi on 2019/7/12.
 * qq 84543217
 */
public class SignActivity extends BaseActivity {

    private Button btn_sign;
    //  private ImageView iv1,iv2,iv3,iv4,iv5,iv6,iv7;
    private ImageView[] imageViews = new ImageView[7];
    private TextView tv_sign;
    private TextView tv_lianxu;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    JSONObject data = (JSONObject) msg.obj;
                    JSONArray list = data.getJSONArray("list");
                    String content = data.getString("content");
                    tv_sign.setText(content);
                    int lianxuInt=0;
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        if (jsonObject.getInteger("signStatus") == 0) {
                            imageViews[i].setImageResource(R.drawable.icon_sign_normal);
                        } else {
                            imageViews[i].setImageResource(R.drawable.icon_sign_finish);
                            lianxuInt++;
                        }
                        tv_lianxu.setText("已签到"+lianxuInt+"天");
                        if (jsonObject.getInteger("signStatus") == 1 && jsonObject.getInteger("isToday") == 1) {
                            //今日已签到
                            btn_sign.setText("今日已签到");
                            btn_sign.setClickable(false);
                        }
                    }
                    if (msg.arg1 == 1) {
                        CommonUtils.showToastShort(SignActivity.this, "签到成功");

                    }


                    break;
                case 1001:
                    int resId = msg.arg1;
                    Toast.makeText(SignActivity.this, resId, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_sign);
        setTitle("签到" );
        btn_sign = this.findViewById(R.id.btn_sign);
        tv_sign = this.findViewById(R.id.tv_sign);
        tv_lianxu= this.findViewById(R.id.tv_lianxu);
        imageViews[0] = this.findViewById(R.id.iv_sign_1);
        imageViews[1] = this.findViewById(R.id.iv_sign_2);
        imageViews[2] = this.findViewById(R.id.iv_sign_3);
        imageViews[3] = this.findViewById(R.id.iv_sign_4);
        imageViews[4] = this.findViewById(R.id.iv_sign_5);
        imageViews[5] = this.findViewById(R.id.iv_sign_6);
        imageViews[6] = this.findViewById(R.id.iv_sign_7);


        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSignDesc(1);
            }
        });

//        // getSignList();
//        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        getSignDesc(0);
    }

//    private void signInServer(){
//        CommonUtils.showDialogNumal(SignActivity.this,"正在签到");
//        List<Param> params=new ArrayList<>();
//        params.add(new Param("userid", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(SignActivity.this).post(params, HTConstant.URL_SET_SIGN, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                int code=jsonObject.getInteger("code");
//                if(code==1){
//                    CommonUtils.showToastShort(SignActivity.this,"签到成功");
//                    btn_sign.setText("今日已签到");
//                    btn_sign.setClickable(false);
//                    getSignList();
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(SignActivity.this,"签到失败");
//
//            }
//        });
//
//
//
//    }

//    private void getSignList(){
//
//        List<Param> params=new ArrayList<>();
//        params.add(new Param("userid", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(SignActivity.this).post(params, HTConstant.URL_GET_SIGN, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code=jsonObject.getInteger("code");
//                if(code==1){
//
//                    JSONObject data=jsonObject.getJSONObject("data");
//                    boolean monday=data.getBoolean("monday");
//                    boolean tuesday=data.getBoolean("tuesday");
//                    boolean wednesday=data.getBoolean("wednesday");
//                    boolean thursday=data.getBoolean("thursday");
//                    boolean friday=data.getBoolean("friday");
//                    boolean saturday=data.getBoolean("saturday");
//                    boolean sunday=data.getBoolean("sunday");
//                     int wkday=data.getInteger("wkday");
//                     if((wkday==0&&sunday)||(wkday==1&&monday)||(wkday==2&&tuesday)||(wkday==3&&wednesday)||(wkday==4&&thursday)||(wkday==5&&friday)||(wkday==6&&saturday)){
//                         btn_sign.setText("今日已签到");
//                         btn_sign.setClickable(false);
//                     }
//
//                     if(monday){
//                         iv1.setImageResource(R.drawable.icon_sign_finish);
//                     }
//                    if(tuesday){
//                        iv2.setImageResource(R.drawable.icon_sign_finish);
//                    }
//                    if(wednesday){
//                        iv3.setImageResource(R.drawable.icon_sign_finish);
//                    }
//
//                    if(thursday){
//                        iv4.setImageResource(R.drawable.icon_sign_finish);
//                    }
//                    if(friday){
//                        iv5.setImageResource(R.drawable.icon_sign_finish);
//                    }
//                    if(saturday){
//                        iv6.setImageResource(R.drawable.icon_sign_finish);
//                    }
//                    if(sunday){
//                        iv7.setImageResource(R.drawable.icon_sign_finish);
//                    }
//
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });
//
//    }


    private void getSignDesc(int type) {
        JSONObject body = new JSONObject();
        body.put("signType", type);
        ApiUtis.getInstance().postJSON(body, Constant.URL_SIGN, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = data;
                    message.arg1 = type;
                    message.sendToTarget();
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


//        List<Param> params = new ArrayList<>();
////        params.add(new Param("userId", HTApp.getInstance().getUsername()));
////        params.add(new Param("money", money));
////        params.add(new Param("bankcard", cardNo));
//        new OkHttpUtils(getBaseContext()).post(params, HTConstant.SIGN_DESC, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        String data=jsonObject.getString("data");
//                        tv_sign.setText(data);
//
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });
//


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
