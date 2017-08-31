package com.htmessage.fanxinht.anyrtc.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by xiongxuesong-pc on 2016/6/23.
 * 权限检查工具类
 */
public class PermissionsCheckUtil {

    public static final int SETTING_APP = 0x123;
    private static final int REQUECT_CODE_FILE = 1;
    private static final int REQUECT_CODE_CAMARE = 2;
    private static String TAG = "PermissionsCheckUtil";
    //包含在该数组的手机品牌可以跳到设置界面直接进行权限修改
    private static String[] PHONE_MTYB = new String[]{"sanxing", "xiaomi"};

    /**
     * 判断当前手机版本是否大于等于Android 6.0
     *
     * @return
     */
    private static boolean thanSDK23() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

    }

    /**
     * 检查摄像头的权限是否打开
     */
    public static void isOpenCarmaPermission(RequestPermissionListener listener) {
        if (thanSDK23()) {
            listener.requestPermissionThanSDK23();
        } else {
            final android.hardware.Camera.Parameters parameters;
            android.hardware.Camera camera = null;
            try {
                camera = android.hardware.Camera.open(0);//前置摄像头
                parameters = camera.getParameters();
                listener.requestPermissionSuccess();
            } catch (RuntimeException e) {
                listener.requestPermissionFailed();

            } finally {
                if (camera != null) {
                    camera.release();
                }
            }
        }
    }

    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    /**
     * 判断是是否有录音权限
     */
    public static boolean isOpenRecordAudioPermission(RequestPermissionListener listener) {
        if (thanSDK23()) {
            listener.requestPermissionThanSDK23();
        } else {
            bufferSizeInBytes = 0;
            bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                    channelConfig, audioFormat);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);
            //开始录制音频
            try {
                // 防止某些手机崩溃，例如联想
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                listener.requestPermissionFailed();
//                e.printStackTrace();
                return false;
            }

            /**
             * 根据开始录音判断是否有录音权限
             */
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                listener.requestPermissionFailed();
                return false;
            }

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            listener.requestPermissionSuccess();
        }
        return true;
    }

    public interface RequestPermissionListener {
        /**
         * 所需权限已打开，当前手机版本小于Android 6.0调用
         */
        void requestPermissionSuccess();

        /**
         * 所需权限未打开，当前手机版本小于Android 6.0调用
         */
        void requestPermissionFailed();

        /**
         * 当前手机版本大于等于Android 6.0调用
         */
        void requestPermissionThanSDK23();
    }

    /**
     * @param activity
     * @param message  显示缺失权限提示说明
     */
    public static void showMissingPermissionDialog(final Activity activity, String message) {
        boolean canSetting = false;
        String mtyb = Build.BRAND;//手机品牌
        for (int i = 0; i < PHONE_MTYB.length; i++) {
            if (PHONE_MTYB[i].equalsIgnoreCase(mtyb)) {//相等可以调用到设置界面进行权限设置
                canSetting = true;
                break;
            } else {
                canSetting = false;
            }
        }
        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        build.setTitle("连麦");
        build.setMessage(message);
        if (canSetting) {
            build.setPositiveButton("设置", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    startAppSettings(activity);
                }
            });
        }
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        build.show();
    }

    // 启动应用的设置
    public static void startAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, SETTING_APP);
    }
}
