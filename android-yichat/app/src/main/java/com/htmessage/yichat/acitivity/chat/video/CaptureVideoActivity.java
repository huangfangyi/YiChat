package com.htmessage.yichat.acitivity.chat.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.weight.videoview.AttachmentStore;
import com.htmessage.yichat.acitivity.chat.weight.videoview.TimeUtil;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 视频录制界面
 * <p/>
 * Created by huangjun on 2015/4/11.
 */
public class CaptureVideoActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = "video";

    private static final String EXTRA_DATA_FILE_NAME = "EXTRA_DATA_FILE_NAME";

    private static final int VIDEO_TIMES = 10;

    private static final int VIDEO_WIDTH = 320;

    private static final int VIDEO_HEIGHT = 240;

    // context

    public Handler handler = new Handler();

    // media

    private MediaRecorder mediaRecorder;// 录制视频的类

    private Camera camera;
    private LinearLayout ll_back;
    // view

    private SurfaceView surfaceview;

    private SurfaceHolder surfaceHolder;

    private ImageView recordBtn;

    private ImageView recordingState;

    private TextView recordingTimeTextView;

    private ImageView switchCamera; // 切换摄像头

    // state

    private int cameraId = 0;

    private String filename;

    private boolean previewing = false;

    private boolean multiCamera = false;

    private boolean recording = false;

    private long start, end; // 录制时间控制

    private long duration = 0;

    private boolean destroyed = false;

    private int mAngle = 0;

    private LinkedList<Point> backCameraSize = new LinkedList<>();

    private LinkedList<Point> frontCameraSize = new LinkedList<>();
    private boolean isMoment = false;


    public static void start(Activity activity, String videoFilePath, int captureCode) {
        Intent intent = new Intent();
        intent.setClass(activity, CaptureVideoActivity.class);
        intent.putExtra(EXTRA_DATA_FILE_NAME, videoFilePath);
        activity.startActivityForResult(intent, captureCode);
    }

    // 录制时间计数
    private Runnable runnable = new Runnable() {

        public void run() {
            end = new Date().getTime();
            duration = (end - start);
            int invs = (int) (duration / 1000);

            recordingTimeTextView.setText(TimeUtil.secToTime(invs));

            // 录制过程中红点闪烁效果
            if (invs % 2 == 0) {
                //  recordingState.setBackgroundResource(R.drawable.nim_record_start);
            } else {
                //  recordingState.setBackgroundResource(R.drawable.nim_record_video);
            }
            if (invs >= VIDEO_TIMES) {
                stopRecorder();
                sendVideo();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT); // 使得窗口支持透明度
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_recoder);
        parseIntent();
        findViews();
        initActionBar();

        setViewsListener();
        updateRecordUI();

        getVideoPreviewSize();

        surfaceview = (SurfaceView) this.findViewById(R.id.videoView);
        SurfaceHolder holder = surfaceview.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);

        resizeSurfaceView();
    }

    private void parseIntent() {
        filename = getIntent().getExtras().getString(EXTRA_DATA_FILE_NAME);
        isMoment = getIntent().getExtras().getBoolean(HTConstant.SMALL_FROM_MOMENT,false);
    }

    private void findViews() {
        recordingTimeTextView = (TextView) findViewById(R.id.record_times);
        recordingState = (ImageView) findViewById(R.id.recording_id);

        recordBtn = (ImageView) findViewById(R.id.record_btn);
        switchCamera = (ImageView) findViewById(R.id.switch_cameras);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
    }

    private void initActionBar() {
        checkMultiCamera();
    }

    private void setViewsListener() {
        recordBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (recording) {
                    stopRecorder();
                    sendVideo();
                } else {
                    startRecorder();
                }
            }
        });
        switchCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        ll_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @TargetApi(9)
    private void switchCamera() {
        if (Build.VERSION.SDK_INT >= 9) {
            cameraId = (cameraId + 1) % Camera.getNumberOfCameras();
        }
        resizeSurfaceView();
        shutdownCamera();
        initCamera();
        startPreview();
    }

    public void onResume() {
        super.onResume();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onPause() {
        super.onPause();

        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (recording) {
            stopRecorder();
            sendVideo();
        } else {
            shutdownCamera();
        }

    }

    public void onDestroy() {
        super.onDestroy();

        shutdownCamera();

        destroyed = true;
    }

    @Override
    public void onBackPressed() {
        if (recording) {
            stopRecorder();
        }

        shutdownCamera();

        setResult(RESULT_CANCELED);
        finish();
    }

    @SuppressLint("NewApi")
    private void getVideoPreviewSize(boolean isFront) {
        CamcorderProfile profile;
        int cameraId = 0;

        if (super.isCompatible(9)) {
            if (isFront) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }

        if (super.isCompatible(11)) {
            if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {
                profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_480P);
                if (profile != null) {
                    Point point = new Point();
                    point.x = profile.videoFrameWidth;
                    point.y = profile.videoFrameHeight;
                    if (isFront) {
                        frontCameraSize.addLast(point);
                    } else {
                        backCameraSize.addLast(point);
                    }
                }
            } else {
                Log.e(TAG, (isFront ? "Back Camera" : "Front Camera") + " no QUALITY_480P");
            }

            if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_CIF)) {
                profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_CIF);
                if (profile != null) {
                    Point point = new Point();
                    point.x = profile.videoFrameWidth;
                    point.y = profile.videoFrameHeight;
                    if (isFront) {
                        frontCameraSize.addLast(point);
                    } else {
                        backCameraSize.addLast(point);
                    }
                }
            } else {
                Log.e(TAG, (isFront ? "Back Camera" : "Front Camera") + " no QUALITY_CIF");
            }

            if (super.isCompatible(15)) {
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QVGA)) {
                    profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_QVGA);
                    if (profile != null) {
                        Point point = new Point();
                        point.x = profile.videoFrameWidth;
                        point.y = profile.videoFrameHeight;
                        if (isFront) {
                            frontCameraSize.addLast(point);
                        } else {
                            backCameraSize.addLast(point);
                        }
                    }
                } else {
                    Log.e(TAG, (isFront ? "Back Camera" : "Front Camera") + " no QUALITY_QVGA");
                }
            }
        }

        if (super.isCompatible(9)) {
            profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
            if (profile == null) {
                Point point = new Point();
                point.x = 320;
                point.y = 240;
                if (isFront) {
                    frontCameraSize.addLast(point);
                } else {
                    backCameraSize.addLast(point);
                }
                Log.e(TAG, (isFront ? "Back Camera" : "Front Camera") + " no QUALITY_LOW");
            } else {
                Point point = new Point();
                point.x = profile.videoFrameWidth;
                point.y = profile.videoFrameHeight;
                if (isFront) {
                    frontCameraSize.addLast(point);
                } else {
                    backCameraSize.addLast(point);
                }
            }
        } else {
            if (!isFront) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                if (profile == null) {
                    Point point = new Point();
                    point.x = 320;
                    point.y = 240;
                    backCameraSize.addLast(point);
                } else {
                    Point point = new Point();
                    point.x = profile.videoFrameWidth;
                    point.y = profile.videoFrameHeight;
                    backCameraSize.addLast(point);
                }
            }
        }
    }


    @SuppressLint("NewApi")
    private void getVideoPreviewSize() {
        backCameraSize.clear();
        frontCameraSize.clear();
        getVideoPreviewSize(false);
        if (Build.VERSION.SDK_INT >= 9) {
            if (Camera.getNumberOfCameras() >= 2) {
                getVideoPreviewSize(true);
            }
        }
    }

    private Point currentUsePoint = null;

    private void resizeSurfaceView() {
        Point point;
        if (cameraId == 0) {
            point = backCameraSize.getFirst();
        } else {
            point = frontCameraSize.getFirst();
        }
        if (currentUsePoint != null && point.equals(currentUsePoint)) {
            return;
        } else {
            currentUsePoint = point;
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            int surfaceHeight = screenWidth * point.x / point.y;
            if (surfaceview != null) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) surfaceview.getLayoutParams();
                lp.width = screenWidth;
                lp.height = surfaceHeight;
                lp.addRule(13);
                surfaceview.setLayoutParams(lp);
            }
        }
    }


    @SuppressLint("NewApi")
    private void setCamcorderProfile() {
        CamcorderProfile profile;
        if (super.isCompatible(11)) {
            if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_CIF)) {
                profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_CIF);
            } else {
                profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
            }
        } else {
            if (super.isCompatible(9)) {
                profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
            } else {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            }
        }

        if (profile != null) {
            if (currentUsePoint != null) {
                profile.videoFrameWidth = currentUsePoint.x;
                profile.videoFrameHeight = currentUsePoint.y;
            }

            profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;

            if (Build.MODEL.equalsIgnoreCase("MB525") || Build.MODEL.equalsIgnoreCase("C8812") || Build.MODEL.equalsIgnoreCase("C8650")) {
                profile.videoCodec = MediaRecorder.VideoEncoder.H263;
            } else {
                profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            }

            if (Build.VERSION.SDK_INT < 11) {
                profile.videoCodec = MediaRecorder.VideoEncoder.H263;
            }

            if (Build.VERSION.SDK_INT >= 14) {
                profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
            } else {
                if (Build.DISPLAY != null && Build.DISPLAY.indexOf("MIUI") >= 0) {
                    profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
                } else {
                    profile.audioCodec = MediaRecorder.AudioEncoder.AMR_NB;
                }
            }
            mediaRecorder.setProfile(profile);
        } else {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        }
    }

    @SuppressLint("NewApi")
    private void setVideoOrientation() {
        if (Build.VERSION.SDK_INT >= 9) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            mediaRecorder.setOrientationHint(info.orientation);
        }
    }

    public void updateRecordUI() {
        if (recording) {
            recordBtn.setBackgroundResource(R.drawable.video_recorder_start_btn);
        } else {
            recordBtn.setBackgroundResource(R.drawable.video_recorder_stop_btn);
        }
    }

    private boolean startRecorderInternal() throws Exception {
        shutdownCamera();
        if (!initCamera())
            return false;

        switchCamera.setVisibility(View.GONE);
        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        setCamcorderProfile();

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setMaxDuration(1000 * VIDEO_TIMES);
        mediaRecorder.setOutputFile(filename);
        setVideoOrientation();

        mediaRecorder.prepare();
        mediaRecorder.start();

        return true;
    }

    private void startRecorder() {
        try {
            startRecorderInternal();
        } catch (Exception e) {
            CommonUtils.showToastShort(this,"录制失败");
            mediaRecorder.release();
            mediaRecorder = null;
            camera.release();
            camera = null;
            return;

        }
        recording = true;
        start = new Date().getTime();
        handler.postDelayed(runnable, 1000);

        recordingTimeTextView.setText("00:00");

        updateRecordUI();
        recordBtn.setBackgroundResource(R.drawable.video_recorder_stop_btn);
    }

    private void stopRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }

        handler.removeCallbacks(runnable);
        recording = false;
        updateRecordUI();
        recordBtn.setBackgroundResource(R.drawable.video_recorder_start_btn);
    }

    private void sendVideo() {
        File convertedFile = new File(filename);
        if (convertedFile.exists()) {
            int b = (int) convertedFile.length();
            int kb = b / 1024;
            float mb = kb / 1024f;
            if (mb <= 1 && kb < 10) {
                tooShortAlert();
                return;
            }
        }
        String st3 = getResources().getString(R.string.Whether_to_send);
        if (isMoment){
            st3 = getResources().getString(R.string.Make_sure_that_the_short_video_is_used);
        }else{
            st3 = getResources().getString(R.string.Whether_to_send);
        }
        showDialog(st3,filename,duration);
    }
    private void showDialog(String content,final String path,final long duration){
        CommonUtils.showAlertDialog(this, null, content, new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {
                Intent intent = new Intent();
                intent.putExtra("dur", duration);
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCancleClock() {
                cancelRecord();
            }
        });
    }
    /**
     * 视频录制太短
     */
    private void tooShortAlert() {
        cancelRecord();
    }

    /**
     * 取消重录
     */
    private void cancelRecord() {
        AttachmentStore.delete(filename);
        recordingTimeTextView.setText("00:00");
        shutdownCamera();
        initCamera();
        startPreview();
        checkMultiCamera();
    }

    /**
     * *************************************************** Camera Start ***************************************************
     */
    @SuppressLint("NewApi")
    public void checkMultiCamera() {
        if (Build.VERSION.SDK_INT >= 9) {
            if (Camera.getNumberOfCameras() > 1) {
                multiCamera = true;
                switchCamera.setVisibility(View.VISIBLE);
            } else {
                switchCamera.setVisibility(View.GONE);
            }
        } else {
            switchCamera.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    private boolean initCamera() {
        try {
            if (multiCamera) {
                camera = Camera.open(cameraId);
            } else {
                camera = Camera.open();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "init camera failed: " + e);
            // Toast.makeText(this, R.string.connect_vedio_device_fail, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (camera != null) {
            setCameraParameters();
        }

        return camera != null;
    }

    @SuppressLint("NewApi")
    private void setCameraParameters() {
        Camera.Parameters params = camera.getParameters();

        if (Build.VERSION.SDK_INT >= 15) {
            if (params.isVideoStabilizationSupported()) {
                params.setVideoStabilization(true);
            }
        }

        List<String> focusMode = params.getSupportedFocusModes();
        if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        if (params != null) {
            mAngle = setCameraDisplayOrientation(this, cameraId, camera);
            Log.i(TAG, "camera angle = " + mAngle);
        }

        params.setPreviewSize(currentUsePoint.x, currentUsePoint.y);

        try {
            camera.setParameters(params);
        } catch (RuntimeException e) {
            Log.e(TAG, "setParameters failed", e);

        }
    }

    private void shutdownCamera() {
        if (camera != null) {
            if (previewing) {
                camera.stopPreview();
            }
            camera.release();
            camera = null;
            previewing = false;
        }
    }


    /**
     * **************************** SurfaceHolder.Callback Start *******************************
     */

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;

        shutdownCamera();
        if (!initCamera())
            return;
        startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = null;
        mediaRecorder = null;
    }

    /**
     * ************************ SurfaceHolder.Callback Start ********************************
     */

    private void startPreview() {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch (Exception e) {
            shutdownCamera();
            e.printStackTrace();
        }
    }

    /**
     * ********************************* camera util ************************************
     */
    @SuppressLint("NewApi")
    public int setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
        int orientation = 90;
        boolean front = (cameraId == 1);
        if (Build.VERSION.SDK_INT >= 9) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            orientation = info.orientation;
            front = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();
        int activityOrientation = roundRotation(rotation);
        int result;
        if (front) {
            result = (orientation + activityOrientation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (orientation - activityOrientation + 360) % 360;
            //遇到过一个小米1s后置摄像头旋转180°，但是不确定是不是所有小米1s都是这样的. 先做一个适配,以后有问题再说.
            if ("Xiaomi_MI-ONE Plus".equalsIgnoreCase(Build.MANUFACTURER + "_" + Build.MODEL)) {
                result = 90;
            }
        }
        camera.setDisplayOrientation(result);
        return result;
    }

    private int roundRotation(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

}