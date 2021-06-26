
package com.htmessage.yichat.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Build.VERSION;
import android.os.PowerManager.WakeLock;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayManager implements SensorEventListener {
    private static final String TAG = "AudioPlayManager";
    private MediaPlayer mMediaPlayer;
    private AudioPlayListener _playListener;
    private Uri mUriPlaying;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager mAudioManager;
    private PowerManager _powerManager;
    private WakeLock _wakeLock;
    private OnAudioFocusChangeListener afChangeListener;
    private boolean isVOIPMode = false;

    public AudioPlayManager() {
    }

    public static AudioPlayManager getInstance() {
        return AudioPlayManager.SingletonHolder.sInstance;
    }

    @TargetApi(11)
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        LoggerUtils.e("AudioPlayManager ------sonSensorChanged. range:" + range + "; max range:" + event.sensor.getMaximumRange());
        if(this._sensor != null && this.mMediaPlayer != null) {
            if(this.mMediaPlayer.isPlaying()) {
                if(range >= event.sensor.getMaximumRange()) {
                    if(this.mAudioManager.getMode() == 0) {
                        return;
                    }

                    this.mAudioManager.setMode(0);
                    this.mAudioManager.setSpeakerphoneOn(true);
                    final int positions = this.mMediaPlayer.getCurrentPosition();

                    try {
                        this.mMediaPlayer.reset();
                        this.mMediaPlayer.setAudioStreamType(3);
                        this.mMediaPlayer.setVolume(1.0F, 1.0F);
                        FileInputStream e = new FileInputStream(this.mUriPlaying.getPath());
                        this.mMediaPlayer.setDataSource(e.getFD());
                        this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                mp.seekTo(positions);
                            }
                        });
                        this.mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                            public void onSeekComplete(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        this.mMediaPlayer.prepareAsync();
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                    this.setScreenOn();
                } else {
                    this.setScreenOff();
                    if(VERSION.SDK_INT >= 11) {
                        if(this.mAudioManager.getMode() == 3) {
                            return;
                        }

                        this.mAudioManager.setMode(3);
                    } else {
                        if(this.mAudioManager.getMode() == 2) {
                            return;
                        }

                        this.mAudioManager.setMode(2);
                    }

                    this.mAudioManager.setSpeakerphoneOn(false);
                    this.replay();
                }
            } else if((double)range > 0.0D) {
                if(this.mAudioManager.getMode() == 0) {
                    return;
                }

                this.mAudioManager.setMode(0);
                this.mAudioManager.setSpeakerphoneOn(true);
                this.setScreenOn();
            }

        }
    }

    @TargetApi(21)
    private void setScreenOff() {
        if(this._wakeLock == null) {
            this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager");
        }

        if(this._wakeLock != null && !this._wakeLock.isHeld()) {
            this._wakeLock.acquire();
        }

    }

    private void setScreenOn() {
        if(this._wakeLock != null && this._wakeLock.isHeld()) {
            this._wakeLock.setReferenceCounted(false);
            this._wakeLock.release();
            this._wakeLock = null;
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void replay() {
        try {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.setAudioStreamType(3);
            this.mMediaPlayer.setVolume(1.0F, 1.0F);
            FileInputStream e = new FileInputStream(this.mUriPlaying.getPath());
            this.mMediaPlayer.setDataSource(e.getFD());
            this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    }

                    mp.start();
                }
            });
            this.mMediaPlayer.prepareAsync();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void startPlay(Context context, Uri audioUri, AudioPlayListener playListener) {
        if(context != null && audioUri != null) {
            if(this._playListener != null && this.mUriPlaying != null) {
                this._playListener.onStop(this.mUriPlaying);
            }

            this.resetMediaPlayer();
            this.afChangeListener = new OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    LoggerUtils.e("AudioPlayManager ------OnAudioFocusChangeListener " + focusChange);
                    if(AudioPlayManager.this.mAudioManager != null && focusChange == -1) {
                        AudioPlayManager.this.mAudioManager.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
                        AudioPlayManager.this.afChangeListener = null;
                        if(AudioPlayManager.this._playListener != null) {
                            AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
                            AudioPlayManager.this._playListener = null;
                        }
                        AudioPlayManager.this.reset();
                    }
                }
            };

            try {
                this._powerManager = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
                this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                if(!this.mAudioManager.isWiredHeadsetOn()) {
                    this._sensorManager = (SensorManager)context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
                    this._sensor = this._sensorManager.getDefaultSensor(8);
                    this._sensorManager.registerListener(this, this._sensor, 3);
                }

                this.muteAudioFocus(this.mAudioManager, true);
                this._playListener = playListener;
                this.mUriPlaying = audioUri;
                this.mMediaPlayer = new MediaPlayer();
                this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        if(AudioPlayManager.this._playListener != null) {
                            AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
                            AudioPlayManager.this._playListener = null;
                        }
                        AudioPlayManager.this.reset();
                    }
                });
                this.mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        AudioPlayManager.this.reset();
                        return true;
                    }
                });
                FileInputStream e = new FileInputStream(audioUri.getPath());
                this.mMediaPlayer.setDataSource(e.getFD());
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.start();
                if(this._playListener != null) {
                    this._playListener.onStart(this.mUriPlaying);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
                if(this._playListener != null) {
                    this._playListener.onStop(audioUri);
                    this._playListener = null;
                }
                AudioPlayManager.this.reset();
            }

        } else {
            LoggerUtils.e("AudioPlayManager ------startPlay context or audioUri is null.");
        }
    }

    public void setPlayListener(AudioPlayListener listener) {
        this._playListener = listener;
    }

    public void stopPlay() {
        if(this._playListener != null && this.mUriPlaying != null) {
            this._playListener.onStop(this.mUriPlaying);
        }

        this.reset();
    }

    private void reset() {
        this.resetMediaPlayer();
        this.resetAudioPlayManager();
    }

    private void resetAudioPlayManager() {
        if(this.mAudioManager != null) {
            this.mAudioManager.setMode(0);
            this.muteAudioFocus(this.mAudioManager, false);
        }

        if(this._sensorManager != null) {
            this.setScreenOn();
            this._sensorManager.unregisterListener(this);
        }

        this._sensorManager = null;
        this._sensor = null;
        this._powerManager = null;
        this.mAudioManager = null;
        this._wakeLock = null;
        this.mUriPlaying = null;
        this._playListener = null;
    }

    private void resetMediaPlayer() {
        if(this.mMediaPlayer != null) {
            try {
                this.mMediaPlayer.stop();
                this.mMediaPlayer.reset();
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
            } catch (IllegalStateException var2) {
                var2.printStackTrace();
            }
        }

    }

    public Uri getPlayingUri() {
        return this.mUriPlaying != null?this.mUriPlaying:Uri.EMPTY;
    }

    @TargetApi(8)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if(bMute) {
            audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
        } else {
            audioManager.abandonAudioFocus(this.afChangeListener);
            this.afChangeListener = null;
        }

    }

    public boolean isInNormalMode(Context context) {
        if(this.mAudioManager == null) {
            this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }

        return this.mAudioManager != null && this.mAudioManager.getMode() == 0;
    }

    public boolean isInVOIPMode(Context context) {
        return this.isVOIPMode;
    }

    public void setInVoipMode(boolean isVOIPMode) {
        this.isVOIPMode = isVOIPMode;
    }

    public boolean isPlaying() {
        return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
    }

    static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();

        SingletonHolder() {
        }
    }
}
