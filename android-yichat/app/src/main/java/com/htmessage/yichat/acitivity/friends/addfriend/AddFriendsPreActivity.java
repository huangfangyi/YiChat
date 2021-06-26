package com.htmessage.yichat.acitivity.friends.addfriend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.friends.invitefriend.ContactsInviteActivity;
import com.htmessage.yichat.acitivity.main.qrcode.MyQrActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.ByteArrayOutputStream;


public class AddFriendsPreActivity extends BaseActivity implements OnClickListener {
    private TextView tv_search, tv_fxid;
    private RelativeLayout rl_leida, rl_jianqun;
    private ImageView iv_scode;
    private String content;
    private String url="";
    private IWXAPI iwxapi;
    private Tencent mTencent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_pre);
        content="我邀请你一起使用YiChat";
        JSONObject jsonObject= SettingsManager.getInstance().getShareJSON();
        if(jsonObject!=null){
            content=jsonObject.getString("content");
            url=jsonObject.getString("androidLink");
        }
        iwxapi = WXAPIFactory.createWXAPI(this, HTConstant.WX_APP_ID, false);
        mTencent = Tencent.createInstance(com.htmessage.update.Constant.QQ_APP_ID, getApplicationContext());

        initUI();
        setOnClick();
    }

    private void initUI() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_fxid = (TextView) findViewById(R.id.tv_fxid);
        rl_leida = (RelativeLayout) findViewById(R.id.rl_leida);
        rl_jianqun = (RelativeLayout) findViewById(R.id.rl_jianqun);

        iv_scode = (ImageView) findViewById(R.id.iv_scode);

        tv_fxid.setText("我的二维码");

    }

    private void setOnClick() {
        tv_search.setOnClickListener(this);
        rl_leida.setOnClickListener(this);
        rl_jianqun.setOnClickListener(this);

        iv_scode.setOnClickListener(this);
        this.findViewById(R.id.rl_weixin).setOnClickListener(this);
        this.findViewById(R.id.rl_qq).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                startActivity(new Intent(AddFriendsPreActivity.this, AddFriendsNextActivity.class));
                break;
            case R.id.rl_leida:
                Intent intent = new Intent(AddFriendsPreActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);

                break;

            case R.id.iv_scode:
                startActivity(new Intent(AddFriendsPreActivity.this, MyQrActivity.class));
                break;
            case R.id.rl_jianqun:
                startActivity(new Intent(AddFriendsPreActivity.this, ContactsInviteActivity.class));

                break;
            case R.id.rl_weixin:

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl =url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "YiChat-熟人之间的交流工具";
                msg.description =content;
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
                bmp.recycle();
                msg.thumbData = Bitmap2Bytes(thumbBmp);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = mTargetScene;
                iwxapi.sendReq(req);
                break;
            case R.id.rl_qq:
                shareToQQ();
                break;
        }
    }

    private Bundle params;

    private void shareToQQ() {
        params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "YiChat-熟人之间的交流工具");// 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");// 网络图片地址params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称");// 应用名称
        // params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其它附加功能");
        // 分享操作要在主线程中完成
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQQ(AddFriendsPreActivity.this, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {

                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        bm.recycle();
        return baos.toByteArray();
    }

    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    private final int REQUEST_CODE_SCAN = 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                CommonUtils.handleQRcodeResult(AddFriendsPreActivity.this, content);


                // result.setText("扫描结果为：" + content);
            }
        }
    }
}
