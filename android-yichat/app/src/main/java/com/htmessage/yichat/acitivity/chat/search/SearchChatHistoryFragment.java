package com.htmessage.yichat.acitivity.chat.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：SearchChatHistoryFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/18 10:51
 * 邮箱:814326663@qq.com
 */
public class SearchChatHistoryFragment extends Fragment {
    private EditText searchView;
    private ImageView iv_clear;
    private ListView listview;
    private String userId;
    private List<HTMessage> allMessages = new ArrayList<>();
    private SearchChatHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        userId = getActivity().getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
        if (TextUtils.isEmpty(userId)) {
            getActivity().finish();
            return;
        }
        HTConversation conversation = HTClient.getInstance().conversationManager().getConversation(userId);
        if (conversation != null) {
            allMessages.clear();
            allMessages.addAll(conversation.getAllMessages());
        }
    }

    private void initView() {
        searchView = (EditText) getView().findViewById(R.id.edt_search);
        listview = (ListView) getView().findViewById(R.id.listview);
        iv_clear = (ImageView) getView().findViewById(R.id.iv_clear);
    }

    private void initData() {
//        refreshListView(allMessages);
    }

    private void setListener() {
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.getText().clear();
                iv_clear.setVisibility(View.GONE);
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    String newText = searchView.getText().toString();
                    List<HTMessage> usersTemp = new ArrayList<HTMessage>();
                    for (HTMessage message : allMessages) {
                        if (message.toXmppMessageBody().contains(newText)) {
                            usersTemp.add(message);
                        }
                    }
                    refreshListView(usersTemp);
                } else {
                    iv_clear.setVisibility(View.GONE);
                    refreshListView(new ArrayList<HTMessage>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void refreshListView(List<HTMessage> allMessages) {
        adapter = new SearchChatHistoryAdapter(allMessages, getActivity(), MessageUtils.CHAT_GROUP);
        listview.setAdapter(adapter);
    }
}
