package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.fanxin.app.fx.others.LoadUserAvatar;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;

public class PayDetailActivity extends BaseActivity {

    private String hxid;
    private LoadUserAvatar avatarLoader;
    private String nick;

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        avatarLoader = new LoadUserAvatar(this, "/sdcard/fanxin/");
        initView();

    }

    private void initView() {

        nick = this.getIntent().getStringExtra("nick");
        final String avater = getIntent().getStringExtra("avatar");
        hxid = getIntent().getStringExtra("hxid");
        
        final EditText et_money = (EditText) this.findViewById(R.id.et_money);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        TextView tv_nick = (TextView) this.findViewById(R.id.tv_nick);
        tv_nick.setText(nick);
        if (avater != null && !avater.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iv_avatar, avater,
                    new ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap) {
                            if (imageView.getTag() == avater) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });

            if (bitmap != null) {

                iv_avatar.setImageBitmap(bitmap);

            }

        }

        Button btn_pay = (Button) this.findViewById(R.id.btn_pay);

        btn_pay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String money = et_money.getText().toString().trim();
                if (money == null) {

                    Toast.makeText(getApplicationContext(), "请输入金额",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isFloathString(money) && !isNumberString(money)) {

                    Toast.makeText(getApplicationContext(), "请输入正确的金额",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                pay(money);
            }

        });

    }

    private void pay(final String money) {
        
        String money_temp=LocalUserInfo.getInstance(getApplicationContext()).getUserInfo("money");
        if(TextUtils.isEmpty(money_temp)){
            money_temp="0";
            
        }
        if(Float.parseFloat(money_temp)<Float.parseFloat(money)){
            
          Toast.makeText(getApplicationContext(), "余额不足,请充值!", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(PayDetailActivity.this);
        dialog.setMessage("正在转账...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        Map<String, String> map = new HashMap<String, String>();

        map.put("hxid", MYApplication.getInstance().getUserName());
        map.put("toHxid", hxid);
        map.put("money", money);
        LoadDataFromServer task = new LoadDataFromServer(
                PayDetailActivity.this, Constant.URL_PAY, map);
        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                dialog.dismiss();
                if (data == null) {
                   
                    Toast.makeText(getApplicationContext(), "服务器访问出错...",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                int code = data.getInteger("code");
                if (code == 1) {

                    String my_money = LocalUserInfo.getInstance(
                            getApplicationContext()).getUserInfo("money");
                    String nowMoney = String.valueOf(Float.parseFloat(my_money)
                            - Float.parseFloat(money));
                    LocalUserInfo.getInstance(getApplicationContext())
                            .setUserInfo("money", nowMoney);
                    Intent intent = new Intent(PayDetailActivity.this,
                            PayResultActivity.class);
                    intent.putExtra("nick", nick);
                    intent.putExtra("money", money);
                    startActivity(intent);
                    finish();

                }
            }

        });

    }

    public boolean isFloathString(String testString) {
        if (!testString.contains(".")) {
            return isNumberString(testString);
        } else {
            String[] floatStringPartArray = testString.split("\\.");
            if (floatStringPartArray.length == 2) {
                if (true == isNumberString(floatStringPartArray[0])
                        && true == isNumberString(floatStringPartArray[1]))
                    return true;
                else
                    return false;
            } else
                return false;

        }

    }

    private boolean isNumberString(String testString) {
        String numAllString = "0123456789";
        if (testString.length() <= 0)
            return false;
        for (int i = 0; i < testString.length(); i++) {
            String charInString = testString.substring(i, i + 1);
            if (!numAllString.contains(charInString))
                return false;
        }
        return true;
    }
}
