package com.fanxin.huangfangyi.ui;

import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.fanxin.easeui.utils.EaseUserUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMSearchDirection;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.fanxin.huangfangyi.R;
import com.hyphenate.util.DateUtils;

public class GroupSearchMessageActivity extends BaseActivity implements OnClickListener{
    private ImageButton clearSearch;
    private EditText query;
    private ListView listView;
    private List<EMMessage> messageList;
    private String groupId;
    private TextView cancleView;
    private TextView searchView;
    private SearchedMessageAdapter messageaAdapter;
    private ProgressDialog pd;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_group_search_message);
        
        query = (EditText) findViewById(R.id.query);
        // clear button
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        listView = (ListView) findViewById(R.id.listview);
        emptyView = (TextView) findViewById(R.id.tv_no_result);
        listView.setEmptyView(emptyView);
        emptyView.setVisibility(View.INVISIBLE);
        cancleView = (TextView) findViewById(R.id.tv_cancel);
        searchView = (TextView) findViewById(R.id.tv_search);
        
        groupId = getIntent().getStringExtra("groupId");
        
        cancleView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                searchView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
                searchView.setText(String.format(getString(R.string.search_contanier), s));
            }
        });
        
        query.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchMessages();
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                searchView.setText("");
            }
        });
        
    }
    
    private void searchMessages(){
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.searching));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupId);
                List<EMMessage> resultList = conversation.searchMsgFromDB(query.getText().toString(), System.currentTimeMillis(), 50, null, EMSearchDirection.UP);
                if(messageList == null){
                    messageList = resultList;
                }else{
                    messageList.clear();
                    messageList.addAll(resultList);
                }
                onSearchResulted();
            }
        }).start();
    }
    
    private void onSearchResulted(){
        runOnUiThread(new Runnable() {
            public void run() {
                pd.dismiss();
                searchView.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                if(messageaAdapter == null){
                    messageaAdapter = new SearchedMessageAdapter(GroupSearchMessageActivity.this, 1, messageList);
                    listView.setAdapter(messageaAdapter);
                }else{
                    messageaAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_cancel:
            finish();
            break;
        case R.id.tv_search:
            hideSoftKeyboard();
            searchMessages();
            break;
        default:
            break;
        }
    }
    
    private class SearchedMessageAdapter extends ArrayAdapter<EMMessage> {

        public SearchedMessageAdapter(Context context, int resource, List<EMMessage> objects) {
            super(context, resource, objects);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.em_row_search_message, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.message = (TextView) convertView.findViewById(R.id.message);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                convertView.setTag(holder);
            }
            
            EMMessage message = getItem(position);
            EaseUserUtils.setUserNick(message.getFrom(), holder.name);
            EaseUserUtils.setUserAvatar(getContext(), message.getFrom(), holder.avatar);
            holder.time.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            holder.message.setText(((EMTextMessageBody)message.getBody()).getMessage());
            
            
            return convertView;
        }
        
    }
    
    private static class ViewHolder {
        TextView name;
        TextView message;
        TextView time;
        ImageView avatar;

    }
    
}
