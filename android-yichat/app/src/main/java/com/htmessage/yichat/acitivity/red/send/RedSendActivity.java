package com.htmessage.yichat.acitivity.red.send;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：Treasure
 * 类描述：RedSendActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/28 13:51
 * 邮箱:814326663@qq.com
 */
public class RedSendActivity extends BaseActivity {
    private int chatType = MessageUtils.CHAT_SINGLE;
    private boolean  isTransfer=false;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        isTransfer=getIntent().getBooleanExtra("isTransfer",false);
                if(isTransfer){
                    setTitle("转账");
                }else{
                    setTitle(R.string.send_red);
                }

         chatType = getIntent().getIntExtra("chatType", MessageUtils.CHAT_SINGLE);
        if (chatType == MessageUtils.CHAT_GROUP){
            RedGroupSendFragment fragment = (RedGroupSendFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
            if (fragment == null) {
                fragment = new RedGroupSendFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.contentFrame, fragment);
                transaction.commit();
            }
        }else{
            RedSendFragment fragment = (RedSendFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
            if (fragment == null) {
                fragment = new RedSendFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.contentFrame, fragment);
                transaction.commit();
                fragment.setArguments(getIntent().getExtras());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode ==RESULT_OK && requestCode ==1000){
            if (data !=null){
                setResult(RESULT_OK,data);
                finish();
            }
        }else if (resultCode == RESULT_CANCELED){
//            CommonUtils.showToastShort(getBaseContext(),"支付取消");
//            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
