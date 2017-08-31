package com.htmessage.fanxinht.acitivity.prevideocall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.chat.call.GroupRtcActivity;
import org.anyrtc.meet_kit.RTMeetKit;

/**
 * 项目名称：yicaht
 * 类描述：PreVideoCallActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/3/15 9:34
 * 邮箱:814326663@qq.com
 */
public class PreVideoCallActivity extends BaseActivity {
    private String groupId = null;
    private boolean isAgain = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        groupId = getIntent().getStringExtra("groupId");
        isAgain = getIntent().getBooleanExtra("isAgain", false);
        if (TextUtils.isEmpty(groupId)){
            finish();
            return;
        }
        setTitle(R.string.check_people);
        PreVideoCallFragment fragment = (PreVideoCallFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment==null){
            fragment = new PreVideoCallFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        final PreVideoCallPrestener prestener = new PreVideoCallPrestener(fragment);
        showRightTextView(R.string.start, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String callId = prestener.getCallId();
                if (!TextUtils.isEmpty(callId)){
                    if (isAgain==false) {
                        Intent intent = new Intent(PreVideoCallActivity.this, GroupRtcActivity.class);
                        intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto.ordinal());
                        intent.putExtra("userId", groupId);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("isGroup", true);
                        intent.putExtra("callId", callId);
                        startActivity(intent);
                    } else {
                        Intent intent1 = new Intent();
                        intent1.putExtra("callId", callId);
                        setResult(RESULT_OK, intent1);
                    }
                    finish();
                }
            }
        });
    }
}
