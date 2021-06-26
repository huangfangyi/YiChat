package com.htmessage.yichat.acitivity.chat;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;


/**
 * Created by huangfangyi on 2017/4/15.
 * qq 84543217
 */

public class VideoIncomingActivity extends BaseActivity {
    private ImageView  ivAvatar ;
    private TextView tvNick,tv_groupname,tv_note;
     public SoundPool soundPool;
    public int streamID = 0;
    public AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_coming);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        intMediaPlayer(R.raw.video_incoming, false);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //    int action = intent.getIntExtra("action", 0);
       // initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
        }
    }


    private void initView() {
         String data=getIntent().getStringExtra("data");
         if(data==null){
             finish();
             return;
         }
         JSONObject dataJSON=JSONObject.parseObject(data);
         if(dataJSON==null){
             finish();
             return;
         }

        ivAvatar = (ImageView) this.findViewById(R.id.iv_avatar);//头像
        UserManager.get().loadUserAvatar(VideoIncomingActivity.this,dataJSON.getString("avatar"),ivAvatar);
        tvNick = (TextView) this.findViewById(R.id.tv_nick);//昵称
        tvNick.setText(dataJSON.getString("nick"));
        tv_note = (TextView) this.findViewById(R.id.tv_note);

        tv_groupname=(TextView) this.findViewById(R.id.tv_groupname);

        tv_groupname.setText(dataJSON.getString("groupName"));
        tv_note.setText(dataJSON.getString("content"));
        this.findViewById(R.id.ll_hulue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final String groupId=dataJSON.getString("groupId");

        this.findViewById(R.id.ll_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoIncomingActivity.this, ChatActivity.class).putExtra("userId",groupId).putExtra("chatType",2));
                finish();
            }
        });

    }


    public void intMediaPlayer(int raw, final boolean isOutiong1) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        }
        streamID = soundPool.load(this, raw, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {

                soundPool.play(1, 1, 1, 0, -1, 1);

            }
        });


    }

}
