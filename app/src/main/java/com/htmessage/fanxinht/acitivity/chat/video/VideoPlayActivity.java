package com.htmessage.fanxinht.acitivity.chat.video;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htmessage.fanxinht.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/**
 * 项目名称：VideoPlayDemo
 * 类描述：VideoPlayActivity 描述:
 * 创建人：songlijie
 * 创建时间：2016/12/19 17:59
 * 邮箱:814326663@qq.com
 */
public class VideoPlayActivity extends AppCompatActivity {
    public static final String VIDEO_NAME = "videoName";
    public static final String VIDEO_PATH = "videoPath";
    private String videoName, videoPath;
    private JCVideoPlayerStandard myVideoView;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉头部title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_play);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        videoName = getIntent().getStringExtra(VIDEO_NAME);
        videoPath = getIntent().getStringExtra(VIDEO_PATH);
    }

    private void setListener() {
        myVideoView.backButton.setVisibility(View.GONE);
    }

    private void initData() {
        myVideoView.setUp(videoPath, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, "");
        myVideoView.startButton.performClick();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        myVideoView = (JCVideoPlayerStandard) findViewById(R.id.vv_paly);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.closeButton:
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
