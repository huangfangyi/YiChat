/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.fanxinht.widget.zxing.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.Result;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.auth.AuthActivity;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.widget.zxing.camera.CameraManager;
import com.htmessage.fanxinht.widget.zxing.decode.DecodeBitmap;
import com.htmessage.fanxinht.widget.zxing.decode.DecodeThread;
import com.htmessage.fanxinht.widget.zxing.utils.BeepManager;
import com.htmessage.fanxinht.widget.zxing.utils.CaptureActivityHandler;
import com.htmessage.fanxinht.widget.zxing.utils.InactivityTimer;
import com.htmessage.fanxinht.widget.zxing.utils.SelectAlbumUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */

public class CaptureActivity extends BaseActivity implements
        SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final int RESULT_MULLT = 5;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private ImageView ivBack;
    private TextView tv_title;
    private int captureType = 1;
    private TextView tvAlbum;
    private static final int CODE_GALLERY_REQUEST = 101;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);
        captureType = getIntent().getIntExtra("type", 0);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tvAlbum = (TextView) findViewById(R.id.tv_capture_select_album);
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相册选择图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
            }
        });
        ivBack.setTag(123);
        ivBack.setOnClickListener(this);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        tv_title.setText(R.string.scan_title);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

//        if (captureType == 0) {
//            Intent resultIntent = new Intent();
//            bundle.putInt("width", mCropRect.width());
//            bundle.putInt("height", mCropRect.height());
//            bundle.putString("result", rawResult.getText());
//            resultIntent.putExtras(bundle);
//            this.setResult(RESULT_OK, resultIntent);
//            CaptureActivity.this.finish();
//        } else {
        scanDeviceSuccess(rawResult.toString(), bundle);
//        }

    }

    /**
     * 扫描设备二维码成功
     *
     * @param rawResult
     * @param bundle
     */
    private void scanDeviceSuccess(String rawResult, Bundle bundle) {
        if (!TextUtils.isEmpty(rawResult)) {
            JSONObject object = JSONObject.parseObject(rawResult);
            int codeType = object.getIntValue("codeType");
            switch (codeType) {
                case 1:
                    JSONObject authObj = object.getJSONObject("data");
                    sendToAuth(authObj);
                    break;
                case 2:
                    JSONObject data = object.getJSONObject("data");
                    startActivity(new Intent(CaptureActivity.this, UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO, data.toJSONString()));
                    break;
                case 3:

                    break;
                case 4:

                    break;
            }
            CaptureActivity.this.finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.code_is_not_invlide, Toast.LENGTH_SHORT).show();
        }
//        Intent resultIntent = new Intent();
//        bundle.putString("result", rawResult);
//        resultIntent.putExtras(bundle);
//        this.setResult(RESULT_OK, resultIntent);
//        CaptureActivity.this.finish();
    }

    private void sendToAuth(JSONObject authobj) {
        String loginId = authobj.getString(HTConstant.JSON_KEY_LOGINID);
        String appname = authobj.getString(HTConstant.JSON_KEY_APPNAME);
        String appicon = authobj.getString(HTConstant.JSON_KEY_APPICON);
        if (TextUtils.isEmpty(appicon)) {
            appicon = "false";
        }
        Intent intent = new Intent(this, AuthActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(HTConstant.JSON_KEY_THIRDAPPNAME, appname); //app的名字
        bundle.putString(HTConstant.JSON_KEY_LOGINID, loginId);
        bundle.putBoolean(HTConstant.JSON_KEY_ISWEB, true); //是否是web授权
        bundle.putString(HTConstant.JSON_KEY_THIRDAPPICON, appicon);//APP的图标 ..为一个URL云端的图片地址
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(getString(R.string.camera_error));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void back(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                Intent resultIntent = new Intent();
                this.setResult(RESULT_CANCELED, resultIntent);
                CaptureActivity.this.finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (Integer.parseInt(v.getTag().toString())) {
            case 123:
                Intent resultIntent = new Intent();
                this.setResult(RESULT_CANCELED, resultIntent);
                CaptureActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CODE_GALLERY_REQUEST) {
            String picPath = SelectAlbumUtils.getPicPath(this, data);
            Result result = DecodeBitmap.scanningImage(picPath);
            if (result == null) {
                Toast.makeText(this, getString(R.string.pic_no_qrcode), Toast.LENGTH_SHORT).show();
            } else {
                beepManager.playBeepSoundAndVibrate();
                String scanResult = DecodeBitmap.parseReuslt(result.toString());
                scanDeviceSuccess(scanResult, new Bundle());
            }
        }
    }
}