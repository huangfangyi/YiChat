package com.htmessage.yichat.acitivity.main.pay.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.pay.paymentdetails.PayMentListActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;

import java.net.URLEncoder;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/27 16:15
 * 邮箱:814326663@qq.com
 */
public class PayMentActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_money, tv_set_money, tv_save_picture, tv_remarks;
    private LinearLayout lly_annal, lly_money;
    private ImageView iv_code,iv_back;
    private String money = "0.00";
    private String paymentMsg = "";
    private Bitmap bitmap = null;
    private int SET_PAY_MENT_MONEY = 30;
    private QrCodePayBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_payment);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        tv_set_money.setOnClickListener(this);
        tv_save_picture.setOnClickListener(this);
        lly_annal.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void initData() {
        tv_title.setText(R.string.money);
        showQrCode(money, paymentMsg);
    }

    private void showQrCode(String money, String message) {
        this.money = money;
        this.paymentMsg = message;
        try {
            bitmap = generateQRCode(getQrCodeString(Validator.formatMoney(money), message));
            iv_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_set_money = (TextView) findViewById(R.id.tv_set_money);
        tv_save_picture = (TextView) findViewById(R.id.tv_save_picture);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);

        lly_annal = (LinearLayout) findViewById(R.id.lly_annal);
        lly_money = (LinearLayout) findViewById(R.id.lly_money);
        iv_code = (ImageView) findViewById(R.id.iv_code);
        iv_back =  (ImageView) findViewById(R.id.iv_back);
    }

    private void getData() {
        receiver = new QrCodePayBroadcastReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(IMAction.QRCODE_IS_PAYED);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, fileter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set_money:
                startActivityForResult(new Intent(PayMentActivity.this, PayMentMoneySetActivity.class), SET_PAY_MENT_MONEY);
                break;
            case R.id.tv_save_picture:
                if (bitmap != null) {
                    saveImage(bitmap);
                } else {
                    showToast(R.string.saving_failed);
                }
                break;
            case R.id.lly_annal:
                startActivity(new Intent(PayMentActivity.this, PayMentListActivity.class));
                break;
            case R.id.iv_back:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 保存图片
     *
     * @param bitmap
     */
    private void saveImage(final Bitmap bitmap) {
        CommonUtils.showDialogNumal(PayMentActivity.this, getString(R.string.saving));
        new Thread() {
            @Override
            public void run() {
                boolean success = CommonUtils.saveImageToGallery(PayMentActivity.this, bitmap);
                if (success) {
                    showToast(R.string.saving_successful);
                } else {
                    showToast(R.string.saving_failed);
                }
            }
        }.start();
    }

    /**
     * 吐司
     *
     * @param resId
     */
    private void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(PayMentActivity.this, resId);
            }
        });
    }

    /**
     * 生成二维码
     *
     * @param matrix
     * @return
     */
    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 生成二维码
     *
     * @param content
     * @return
     * @throws WriterException
     */
    private Bitmap generateQRCode(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
                500, 500);
        return bitMatrix2Bitmap(matrix);
    }

    /**
     * 获取QRCODE
     *
     * @param money
     * @param msg
     * @return
     */
    private String getQrCodeString(String money, String msg) {
        if ("0.00".equals(Validator.formatMoney(money)) || TextUtils.isEmpty(money)) {
            lly_money.setVisibility(View.GONE);
        } else {
            lly_money.setVisibility(View.VISIBLE);
            tv_money.setText(Validator.formatMoney(money));
        }
        if (TextUtils.isEmpty(msg)) {
            tv_remarks.setVisibility(View.GONE);
        } else {
            tv_remarks.setText(msg);
            tv_remarks.setVisibility(View.VISIBLE);
        }
        JSONObject allobj = new JSONObject();
        allobj.put("codeType", 5);
        JSONObject object = new JSONObject();
        object.put("userId", HTApp.getInstance().getUsername());
        object.put("nick", HTApp.getInstance().getUserNick());
         object.put("money", money);
        object.put("content", msg);
        String encode = URLEncoder.encode(object.toJSONString());
        allobj.put("data", encode);
        return allobj.toJSONString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SET_PAY_MENT_MONEY) {
            if (data != null) {
                String money = data.getStringExtra("money");
                String message = data.getStringExtra("message");
                if (TextUtils.isEmpty(money)) {
                    money = Validator.formatMoney(0);
                }
                if (TextUtils.isEmpty(message)) {
                    message = "";
                }
                showQrCode(money, message);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private class QrCodePayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.QRCODE_IS_PAYED.equals(intent.getAction())){
                String nick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                if (PayMentActivity.class !=null){
                    CommonUtils.showAlertDialogNoCancle(PayMentActivity.this, getString(R.string.prompt), String.format(getString(R.string.has_get_qrpay_from), nick));
                }
            }
        }
    }
}
