package com.htmessage.yichat.acitivity.chat.search;

import android.os.Bundle;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

public class SearchChatHistoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.search_chat_history);
        SearchChatHistoryFragment fragment = (SearchChatHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment ==null){
            fragment = new SearchChatHistoryFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame,fragment).commit();
        }
    }
}
