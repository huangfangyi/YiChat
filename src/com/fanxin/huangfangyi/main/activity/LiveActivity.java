package com.fanxin.huangfangyi.main.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.ulive.play.VideoActivity;
import com.fanxin.huangfangyi.main.ulive.preference.Settings;
import com.fanxin.huangfangyi.main.ulive.preference.SettingsActivity;
import com.fanxin.huangfangyi.main.ulive.upload.PublishDemo4MediaCodec;
import com.fanxin.huangfangyi.main.ulive.upload.PublishDemo4X264;
import com.ucloud.common.util.DeviceUtils;
import com.ucloud.live.UEasyStreaming;



public class LiveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ULiveDemo";

    FloatingActionButton mPublishfab;
    FloatingActionButton mPlayfab;
    EditText publishStreamEdtx;
    Settings mSettings ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        if (publishStreamEdtx != null && mSettings != null) {
            String streamId = DemoApplication.getRandomStreamId() +""; //   mSettings.getPusblishStreamId()
            if (!TextUtils.isEmpty(streamId)) {
                publishStreamEdtx.setText(streamId);
            }
        }
    }

    private void initView() {
        mSettings = new Settings(this);
        mPublishfab = (FloatingActionButton) findViewById(R.id.publish_fab);
        mPlayfab = (FloatingActionButton) findViewById(R.id.play_fab);
        publishStreamEdtx = (EditText) findViewById(R.id.stream_id_et);
        mPublishfab.setOnClickListener(this);
        mPlayfab.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (publishStreamEdtx != null) {
            String streamId = publishStreamEdtx.getText().toString();
            if (mSettings != null && !TextUtils.isEmpty(streamId)) {
                mSettings.setPublishStreamId(streamId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SettingsActivity.intentTo(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String streamId = publishStreamEdtx.getText().toString();
        if (TextUtils.isEmpty(streamId)) {
            Toast.makeText(this, "please input stream id.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.publish_fab) {
            Intent intent = new Intent();
            UEasyStreaming.UEncodingType encoderType = mSettings.getEncoderType();
            switch (encoderType) {
                case MEDIA_CODEC:
                    if (DeviceUtils.hasJellyBeanMr2()) {
                        intent.setClass(getApplicationContext(), PublishDemo4MediaCodec.class);//API 18+ support
                    } else {
                        Toast.makeText(this, "medicodec only support after android API18+, auto toggle to x264", Toast.LENGTH_SHORT).show();
                        intent.setClass(getApplicationContext(), PublishDemo4X264.class); //API 8+
                    }
                    break;
                case MEDIA_X264:
                    intent.setClass(getApplicationContext(), PublishDemo4X264.class); //API 8+
                    break;
            }
            startActivity(intent);
        } else if (v.getId() == R.id.play_fab) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),VideoActivity.class);
            startActivity(intent);
        }
    }
}
