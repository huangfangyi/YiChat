package com.htmessage.fanxinht.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.htmessage.sdk.model.HTMessage;

/**
 * Created by huangfangyi on 2016/10/12.
 * qq 84543217
 */

public class NotifierManager {

    private Ringtone ringtone = null;
    private long lastNotifiyTime;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private HTNotification htNotification;
    private static NotifierManager notifierManager = null;
    /**
     * application context
     */
    private Context appContext;

    /**
     * get notifierManager of EaseUI
     *
     * @return
     */
    public static NotifierManager getInstance() {
        if (notifierManager == null) {
            throw new RuntimeException("NotifierManager please init first!");
        }
        return notifierManager;
    }


    public static synchronized void init(Context context) {
        if (notifierManager == null) {

            notifierManager = new NotifierManager(context);
        }

    }

    public NotifierManager(Context context) {
        appContext = context;
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        htNotification = new HTNotification(appContext);

    }

    public void onNewMessage(HTMessage htMessage) {
        htNotification.onNewMessage(htMessage);
        vibrateAndPlayTone();
    }

    public void cancel(int id) {
        htNotification.cancel(id);
    }

    /**
     * vibrate and  play tone
     */
    public void vibrateAndPlayTone() {
        if (!SettingsManager.getInstance().getSettingMsgNotification()) {//未打开新消息提醒
            return;
        }
        if (!SettingsManager.getInstance().getSettingMsgSound() && !SettingsManager.getInstance().getSettingMsgVibrate()) {//声音或者震动都未打开
            return;
        }
        if (System.currentTimeMillis() - lastNotifiyTime < 1000) { //时间间隔小于1秒钟的返回
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        if (SettingsManager.getInstance().getSettingMsgSound() && !SettingsManager.getInstance().getSettingMsgVibrate()) {//打开了声音
            playSound();
        } else if (SettingsManager.getInstance().getSettingMsgVibrate() && !SettingsManager.getInstance().getSettingMsgSound()) {//打开了震动
            playVibrator();
        } else { //声音和震动都打开了
            playSoundAndVibrator();
        }
    }

    /**
     * 播放声音
     */
    private void playSound() {
        try {
            lastNotifiyTime = System.currentTimeMillis();
            // check if in silent mode
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return;
            }
            if (ringtone == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                if (ringtone == null) {
                    return;
                }
            }
            if (!ringtone.isPlaying()) {
                String vendor = Build.MANUFACTURER;
                ringtone.play();
                // for samsung S3, we meet a bug that the phone will
                // continue ringtone without stop
                // so add below special handler to stop it after 3s if
                // needed
                if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                    Thread ctlThread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (ringtone.isPlaying()) {
                                    ringtone.stop();
                                }
                            } catch (Exception e) {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 声音和震动
     */
    private void playSoundAndVibrator() {
        try {
            lastNotifiyTime = System.currentTimeMillis();
            // check if in silent mode
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return;
            }
            if (vibrator == null) {
                vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (ringtone == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                if (ringtone == null) {
                    return;
                }
            }
            /**
             * 四个参数就是——停止 开启 停止 开启
             * -1不重复，非-1为从pattern的指定下标开始重复
             */
            long[] pattern = new long[]{0, 180, 80, 120};
            vibrator.vibrate(pattern, -1);
            /**
             * 播放声音
             */
            if (!ringtone.isPlaying()) {
                String vendor = Build.MANUFACTURER;
                ringtone.play();
                // for samsung S3, we meet a bug that the phone will
                // continue ringtone without stop
                // so add below special handler to stop it after 3s if
                // needed
                if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                    Thread ctlThread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (ringtone.isPlaying()) {
                                    ringtone.stop();
                                }
                            } catch (Exception e) {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 震动
     */
    private void playVibrator() {

        lastNotifiyTime = System.currentTimeMillis();
        if (vibrator == null) {
            vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
        /**
         * 四个参数就是——停止 开启 停止 开启
         * -1不重复，非-1为从pattern的指定下标开始重复
         */
        long[] pattern = new long[]{0, 180, 80, 120};
        vibrator.vibrate(pattern, -1);
    }



}
