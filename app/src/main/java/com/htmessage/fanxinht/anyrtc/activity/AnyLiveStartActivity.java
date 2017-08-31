package com.htmessage.fanxinht.anyrtc.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.anyrtc.Utils.PermissionsCheckUtil;
import com.htmessage.fanxinht.anyrtc.Utils.RTMPCHttpSDK;
import com.htmessage.fanxinht.HTConstant;
import com.zhy.m.permission.MPermissions;

import org.json.JSONException;

/**
 * 项目名称：FanXin3.0
 * 类描述：AnyLiveStartActivity 描述: 开始直播界面
 * 创建人：songlijie
 * 创建时间：2016/11/9 15:02
 * 邮箱:814326663@qq.com
 */
public class AnyLiveStartActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_center_image;
    private TextView tv_nickname;
    private JSONObject jsonObject;
    private ImageView iv_back, iv_camera;
    private TextView tv_title;
    private RelativeLayout titleBar;
    private String nick;
    private EditText et_theme;
    private Button btn_start;
    private static final int REQUECT_CODE_RECORD = 0;
    private static final int REQUECT_CODE_CAMERA = 1;
    private String avatarUrl;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_anylivestart);
        getDate();
        initView();
        getDevicePermission();
        iniData();
        setOnClick();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getDate() {
        jsonObject = HTApp.getInstance().getUserJson();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        tv_title = (TextView) findViewById(R.id.tv_title);
        titleBar = (RelativeLayout) findViewById(R.id.title);

        iv_center_image = (ImageView) findViewById(R.id.iv_center_image);
        et_theme = (EditText) findViewById(R.id.et_theme);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void iniData() {
        tv_title.setText(R.string.prompt_live_topic);
        iv_camera.setVisibility(View.GONE);
        avatarUrl = jsonObject.getString(HTConstant.JSON_KEY_AVATAR);
        if (!TextUtils.isEmpty(avatarUrl)) {
            if (!avatarUrl.contains("http:")) {
                avatarUrl = HTConstant.URL_AVATAR + avatarUrl;
            }
        } else {
            avatarUrl = HTApp.getInstance().getUsername();
        }
        Glide.with(this).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(iv_center_image);
        nick = jsonObject.getString(HTConstant.JSON_KEY_NICK);
        if (TextUtils.isEmpty(nick)) {
            nick = HTApp.getInstance().getUsername();
        }
        tv_nickname.setText(nick);
    }

    private void setOnClick() {
        btn_start.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    /**
     * 获取摄像头和录音权限
     */
    private void getDevicePermission() {
        PermissionsCheckUtil.isOpenCarmaPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(AnyLiveStartActivity.this, getString(R.string.str_no_camera_permission));
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(AnyLiveStartActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(AnyLiveStartActivity.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
                }
            }
        });


        PermissionsCheckUtil.isOpenRecordAudioPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(AnyLiveStartActivity.this, getString(R.string.str_no_audio_record_permission));
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(AnyLiveStartActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(AnyLiveStartActivity.this, REQUECT_CODE_RECORD, Manifest.permission.RECORD_AUDIO);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                back(v);
                break;
            case R.id.btn_start:
                String trim = et_theme.getText().toString().trim();
                if (TextUtils.isEmpty(trim) || trim.length() == 0) {
                    Toast.makeText(this, getString(R.string.live_theme_not_null), Toast.LENGTH_SHORT).show();
                    return;
                } else if (trim.length() >= 30) {
                    Toast.makeText(this, R.string.Maximum_word_limit_has_been_reached, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String anyrtcId = RTMPCHttpSDK.getRandomString(12);
                    String hostID = jsonObject.getString(HTConstant.JSON_KEY_HXID);
                    String rtmpPushUrl = String.format(HTConstant.RTMP_PUSH_URL, anyrtcId);
                    String rtmpPullUrl = String.format(HTConstant.RTMP_PULL_URL, anyrtcId);
                    String hlsUrl = String.format(HTConstant.HLS_URL, anyrtcId);
                    org.json.JSONObject item = new org.json.JSONObject();
                    try {
                        item.put("hosterId", hostID);
                        item.put("rtmp_url", rtmpPullUrl);
                        item.put("hls_url", hlsUrl);
                        item.put("topic", trim);
                        item.put("nickname", nick);
                        item.put("headUrl", avatarUrl);
                        item.put("anyrtcId", anyrtcId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("hosterId", hostID);
                    bundle.putString("rtmp_url", rtmpPushUrl);
                    bundle.putString("hls_url", hlsUrl);
                    bundle.putString("topic", trim);
                    bundle.putString("headUrl", avatarUrl);
                    bundle.putString("nickname", nick);
                    bundle.putString("andyrtcId", anyrtcId);
                    bundle.putString("userData", item.toString());
                    Intent intent = new Intent(AnyLiveStartActivity.this, AnyHosterActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AnyLiveStart Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
