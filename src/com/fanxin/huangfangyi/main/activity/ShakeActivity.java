/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；

package com.fanxin.huangfangyi.main.activity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OKHttpUtils;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：FanXin3.0
 * 类描述：ShakeActivity 描述:摇一摇
 * 创建人：songlijie
 * 创建时间：2016/11/2 10:13
 * 邮箱:814326663@qq.com
 */
public class ShakeActivity extends BaseActivity implements View.OnClickListener,SensorEventListener {
    private static final String TAG = ShakeActivity.class.getSimpleName();
    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;

//    private boolean isRefresh = false;
    private LinearLayout ll_show_progress;

    private SensorManager mSensorManager;//sensor管理器
    private Sensor mAccelerometerSensor;
    private Vibrator mVibrator;//手机震动
    private SoundPool mSoundPool;//摇一摇音效
    //记录摇动状态
    private boolean isShake = false;
    private RelativeLayout mTopLayout;
    private RelativeLayout mBottomLayout;
    private ImageView mTopLine,iv_people,iv_song,iv_tv;
    private ImageView mBottomLine,iv_center_image;
    private ImageView iv_back,iv_camera;
    private TextView tv_title,tv_people,tv_song,tv_tv,tv_message;
    private RelativeLayout titleBar;
    private MyHandler mHandler;
    private int mWeiChatAudio;
    private String userId = null;
    //记录选择的是那个item
    private boolean isMusic = false;
    private boolean isPeople = true;
    private boolean isTv = false;
    private String type = "people";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //设置只竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_shake);
        getData();
        initViews();
        initData();
        setOnClick();
    }

    private void getData() {
        userId = getIntent().getStringExtra("userID");
        mHandler = new MyHandler(this);
        //初始化SoundPool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mWeiChatAudio = mSoundPool.load(this, R.raw.weichat_audio, 1);
        //获取Vibrator震动服务
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initViews() {
        mTopLayout = (RelativeLayout) findViewById(R.id.main_linear_top);
        mBottomLayout = (RelativeLayout) findViewById(R.id.main_linear_bottom);
        mTopLine = (ImageView) findViewById(R.id.main_shake_top_line);
        mBottomLine = (ImageView) findViewById(R.id.main_shake_bottom_line);
        iv_center_image = (ImageView) findViewById(R.id.iv_center_image);
        ll_show_progress = (LinearLayout) findViewById(R.id.ll_show_progress);
        tv_message = (TextView) findViewById(R.id.tv_message);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        tv_title = (TextView) findViewById(R.id.tv_title);
        titleBar = (RelativeLayout) findViewById(R.id.title);

        iv_people = (ImageView) findViewById(R.id.iv_people);
        tv_people = (TextView) findViewById(R.id.tv_people);
        iv_song = (ImageView) findViewById(R.id.iv_song);
        tv_song = (TextView) findViewById(R.id.tv_song);
        iv_tv = (ImageView) findViewById(R.id.iv_tv);
        tv_tv = (TextView) findViewById(R.id.tv_tv);
        //设置默认人被选择
        iv_people.setBackgroundResource(R.drawable.peoplepress);
        tv_people.setTextColor(getResources().getColor(R.color.btn_green_pressed));
    }

    private void initData() {
        //默认
        mTopLine.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);

        //标题上面的操作
        iv_camera.setVisibility(View.GONE);
        tv_title.setText(getString(R.string.shake_shake));
        //设置中心图片
        iv_center_image.setBackgroundResource(R.drawable.shakebg);
    }

    private void setOnClick() {
        iv_back.setOnClickListener(this);

        iv_people.setOnClickListener(this);
        tv_people.setOnClickListener(this);
        iv_song.setOnClickListener(this);
        tv_song.setOnClickListener(this);
        iv_tv.setOnClickListener(this);
        tv_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                back(v);
                break;
            case R.id.iv_people:
            case R.id.tv_people:
                isPeople = true;
                isMusic = false;
                isTv = false;
                iv_tv.setBackgroundResource(R.drawable.tvnum);
                iv_song.setBackgroundResource(R.drawable.musicnum);
                iv_people.setBackgroundResource(R.drawable.peoplepress);
                tv_tv.setTextColor(getResources().getColor(R.color.white));
                tv_song.setTextColor(getResources().getColor(R.color.white));
                tv_people.setTextColor(getResources().getColor(R.color.btn_green_pressed));
                break;
            case R.id.iv_song:
            case R.id.tv_song:
                isPeople = false;
                isMusic = true;
                isTv = false;
                iv_tv.setBackgroundResource(R.drawable.tvnum);
                iv_song.setBackgroundResource(R.drawable.musicpress);
                iv_people.setBackgroundResource(R.drawable.peoplenum);
                tv_people.setTextColor(getResources().getColor(R.color.white));
                tv_tv.setTextColor(getResources().getColor(R.color.white));
                tv_song.setTextColor(getResources().getColor(R.color.btn_green_pressed));
                break;
            case R.id.iv_tv:
            case R.id.tv_tv:
                isPeople = false;
                isMusic = false;
                isTv = true;
                iv_tv.setBackgroundResource(R.drawable.tvpress);
                iv_song.setBackgroundResource(R.drawable.musicnum);
                iv_people.setBackgroundResource(R.drawable.peoplenum);
                tv_people.setTextColor(getResources().getColor(R.color.white));
                tv_song.setTextColor(getResources().getColor(R.color.white));
                tv_tv.setTextColor(getResources().getColor(R.color.btn_green_pressed));
                break;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //获取 SensorManager 负责管理传感器
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        if (mSensorManager != null) {
            //获取加速度传感器
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                //根据不同应用，需要的反应速率不同，具体根据实际情况设定
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    // SensorEventListener回调方法
    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
//            // 判断是否处于刷新状态(例如微信中的查找附近人)
//            if (isRefresh) {
//                return;
//            }
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            if ((Math.abs(x) > 20 || Math.abs(y) > 20 || Math
                    .abs(z) > 20) && !isShake) {
                isShake = true;
                // TODO: 2016/10/19 实现摇动逻辑, 摇动后进行震动
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d(TAG, "onSensorChanged: 摇动");
                            //开始震动 发出提示音 展示动画效果
                            mHandler.obtainMessage(START_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            //再来一次震动提示
                            mHandler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mHandler.obtainMessage(END_SHAKE).sendToTarget();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MyHandler extends Handler {
        private WeakReference<ShakeActivity> mReference;
        private ShakeActivity mActivity;
        public MyHandler(ShakeActivity activity) {
            mReference = new WeakReference<ShakeActivity>(activity);
            if (mReference != null) {
                mActivity = mReference.get();
            }
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    //This method requires the caller to hold the permission VIBRATE.
                    mActivity.mVibrator.vibrate(300);
                    ll_show_progress.setVisibility(View.GONE);
                    mActivity.mTopLine.setVisibility(View.VISIBLE);
                    mActivity.mBottomLine.setVisibility(View.VISIBLE);
                    iv_center_image.setVisibility(View.VISIBLE);
//                    isRefresh = true;
                    //发出提示音
                    mActivity.mSoundPool.play(mActivity.mWeiChatAudio, 1, 1, 0, 0, 1);
                    mActivity.startAnimation(false);//参数含义: (不是回来) 也就是说两张图片分散开的动画
                    break;
                case AGAIN_SHAKE:
                    mActivity.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    //整体效果结束, 将震动设置为false
                    mActivity.isShake = false;
                    // 展示上下两种图片回来的效果
                    mActivity.startAnimation(true);
//                    iv_center_image.setVisibility(View.INVISIBLE);
                    //TODO 动画结束 做一个数据请求,然后获取同一时间摇一摇的人 获取结束后展示出来再隐藏网络请求状态显示栏
//                    ll_show_progress.setVisibility(View.VISIBLE);
                    if (isPeople){  //TODO 请求获取同一时间摇一摇的人
                        type="people";
                    }
                    if (isMusic){ // TODO 需要增加语音识别并把识别出来的文字进行网络请求,然后展示搜索到的音乐
                        type="music";
                    }
                    if (isTv){ // TODO 需要增加语音识别并把识别出来的文字进行网络请求,然后展示搜索到的电视节目
                        type="tv";
                    }
//                    getShakeMessages(type);
                    break;
            }
        }

    }

    private void getShakeMessages(String type) {
        List<Param>  params = new ArrayList<>();
        params.add(new Param("type",type));
        new OKHttpUtils(ShakeActivity.this).post(params, FXConstant.HOST, new OKHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //TODO 此处做网络请求的模拟测试
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
    /**
     * 开启 摇一摇动画
     *
     * @param isBack 是否是返回初识状态
     */
    private void startAnimation(final boolean isBack) {
        //动画坐标移动的位置的类型是相对自己的
        int type = Animation.RELATIVE_TO_SELF;

        float topFromY;
        float topToY;
        float bottomFromY;
        float bottomToY;
        if (isBack) {
            topFromY = -0.5f;
            topToY = 0;
            bottomFromY = 0.5f;
            bottomToY = 0;
        } else {
            topFromY = 0;
            topToY = -0.5f;
            bottomFromY = 0;
            bottomToY = 0.5f;
        }

        //上面图片的动画效果
        TranslateAnimation topAnim = new TranslateAnimation(
                type, 0, type, 0, type, topFromY, type, topToY
        );
        topAnim.setDuration(200);
        //动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        topAnim.setFillAfter(true);

        //底部的动画效果
        TranslateAnimation bottomAnim = new TranslateAnimation(
                type, 0, type, 0, type, bottomFromY, type, bottomToY
        );
        bottomAnim.setDuration(200);
        bottomAnim.setFillAfter(true);

        //大家一定不要忘记, 当要回来时, 我们中间的两根线需要GONE掉
        if (isBack) {
            bottomAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    //TODO 当动画结束后 , 将中间两条线GONE掉, 不让其占位
                    mTopLine.setVisibility(View.GONE);
                    mBottomLine.setVisibility(View.GONE);
                    // TODO 当动画结束时 需要把网络请求状态显示出来,就设置显示
                    ll_show_progress.setVisibility(View.VISIBLE);
                    if (isPeople){
                        tv_message.setText(R.string.shake_people);
                    }
                    if (isMusic){
                        tv_message.setText(R.string.shake_music);
                    }
                    if (isTv){
                        tv_message.setText(R.string.shake_tv);
                    }
                    // TODO 当动画结束时 需要把中间图片隐藏出来,就设置INVISIBLE
                    iv_center_image.setVisibility(View.INVISIBLE);
                }
            });
        }
        //设置动画
        mTopLayout.startAnimation(topAnim);
        mBottomLayout.startAnimation(bottomAnim);

    }
}
