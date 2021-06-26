package com.htmessage.yichat.acitivity.chat.card;

import android.os.Bundle;

  import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：CheckCardActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/17 11:53
 * 邮箱:814326663@qq.com
 */
public class CheckCardActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.Select_the_contact);
        CheckCardFragment fragmet = (CheckCardFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragmet == null) {
            fragmet = new CheckCardFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragmet).commit();
        }
        new CheckCardPresenter(fragmet);
    }
}
