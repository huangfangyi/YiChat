package com.htmessage.yichat.acitivity.chat.video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import cn.jzvd.JzvdStd;


/**
 * 项目名称：ktz
 * 类描述：VideoPlayActivity 描述:视频播放界面
 * 创建人：songlijie
 * 创建时间：2016/12/19 17:59
 * 邮箱:814326663@qq.com
 */
public class VideoPlayActivity extends AppCompatActivity {
    public static final String VIDEO_NAME = "videoName";
    public static final String VIDEO_PATH = "videoPath";
    private String videoName, videoPath;
    private JzvdStd myVideoView;
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
        myVideoView.thumbImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v);
                return true;
            }
        });
        myVideoView.findViewById(R.id.surface_container).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v);
                return true;
            }
        });
    }

    private void initData() {
        myVideoView.setUp(videoPath, "", JzvdStd.SCREEN_FULLSCREEN);
        myVideoView.startButton.performClick();
        myVideoView.startVideo();
        myVideoView.backButton.setVisibility(View.GONE);
        myVideoView.tinyBackImageView.setVisibility(View.GONE);
        myVideoView.batteryLevel.setVisibility(View.GONE);
        myVideoView.batteryTimeLayout.setVisibility(View.GONE);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        myVideoView = (JzvdStd) findViewById(R.id.vv_paly);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JzvdStd.backPress()) {
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
    private void showDialog(final View view) {
        HTAlertDialog dialog = new HTAlertDialog(VideoPlayActivity.this, null, new String[]{getString(R.string.save)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0://保存的判断
                        saveVideo(videoPath,videoName);
                        break;
                }
            }
        });
    }

    @NonNull
    private String getSaveVideoPath(String fileName) {
        String dirFilePath = HTApp.getInstance().getVideoPath();
        return dirFilePath + "/" + fileName;
    }

    /**
     * 保存视频
     *
     * @param filePath
     * @param fileName
     */
    private void saveVideo(final String filePath, String fileName) {
        if (ActivityCompat.checkSelfPermission(VideoPlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||ActivityCompat.checkSelfPermission(VideoPlayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showToast(R.string.need_sdcard_permission);
            return;
        }
        CommonUtils.showDialogNumal(VideoPlayActivity.this, getString(R.string.saving));
        if (TextUtils.isEmpty(filePath)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast(R.string.saving_failed);
                }
            }, 500);
            return;
        }
        final String savePath = getSaveVideoPath(fileName);
        if (!filePath.startsWith("http") || !filePath.contains(HTConstant.baseImgUrl)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean file = CommonUtils.copyFile(VideoPlayActivity.this, filePath, savePath);
                    if (file){
                        showToast(R.string.saving_successful);
                    }else{
                        showToast(R.string.saving_failed);
                    }
                }
            }, 500);
        }else{
            new OkHttpUtils(VideoPlayActivity.this).loadFile(filePath, savePath, new OkHttpUtils.DownloadCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //通知图库更新
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
                                Uri uri = Uri.parse(savePath);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            } else {
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                            }
                        }
                    });
                    showToast(R.string.saving_successful);
                }

                @Override
                public void onFailure(String message) {
                    showToast(R.string.saving_failed);
                }
            });
        }
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
                CommonUtils.showToastShort(getBaseContext(), resId);
            }
        });
    }
}
