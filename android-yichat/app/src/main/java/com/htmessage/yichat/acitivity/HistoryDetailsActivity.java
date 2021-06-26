package com.htmessage.yichat.acitivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.sdk.ChatType;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huangfangyi on 2019/5/16.
 * qq 84543217
 */
public class HistoryDetailsActivity extends BaseActivity {

    private EditText etSearch;
    private TextView tvSearch;
    private ListView listView;
    private List<HTMessage> htMessages = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_history_detais);
        etSearch = findViewById(R.id.et_search);
        tvSearch = findViewById(R.id.tv_search);
        listView = findViewById(R.id.listview);

        //        final String userId=this.getIntent().getStringExtra("userId");
//        final int chatType=this.getIntent().getIntExtra("chatType",0);
        htMessages = this.getIntent().getParcelableArrayListExtra("data");
        adapter = new MyAdapter(htMessages, HistoryDetailsActivity.this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HTMessage htMessage=adapter.getItem(position);
                startActivity(new Intent(HistoryDetailsActivity.this, ChatActivity.class)
                .putExtra("chatType", htMessage.getChatType().ordinal()+1)
                        .putExtra("userId",htMessage.getUsername())
                );
            }
        });
    }


    class MyAdapter extends BaseAdapter {
        private List<HTMessage> data;
        private Context context;

        public MyAdapter(List<HTMessage> data, Context context) {

            this.data = data;
            this.context = context;
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public HTMessage getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_message_search, parent, false);

            }

            ImageView avatarView = convertView.findViewById(R.id.avatar);
            TextView nameView = convertView.findViewById(R.id.name);
            TextView contentView = convertView.findViewById(R.id.message);
            TextView timeView = convertView.findViewById(R.id.time);


            String nick = getItem(position).getStringAttribute("nick");
            String avatar = getItem(position).getStringAttribute("avatar");
            String content = ((HTMessageTextBody) getItem(position).getBody()).getContent();
            long time = getItem(position).getTime();
            UserManager.get().loadUserAvatar(HistoryDetailsActivity.this, avatar, avatarView);
            nameView.setText(nick);
            contentView.setText(content);
            timeView.setText(DateUtils.getTimestampString(new Date(time)));
            return convertView;
        }
    }


}
