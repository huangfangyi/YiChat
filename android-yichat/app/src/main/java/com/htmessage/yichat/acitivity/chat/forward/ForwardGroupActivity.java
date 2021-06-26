package com.htmessage.yichat.acitivity.chat.forward;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ForwardGroupActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected List<HTGroup> grouplist = new ArrayList<>();
    protected List<HTGroup> checkGroupList = new ArrayList<>();
    private TextView tv_group_check, tv_title;
    private ListView list;
    private ForwardGroupAdapter adAdapter;

    private HTMessage messageOrigin;
    private Button btn_rtc;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_check_people);
        getData();
        initView();
        initData();
        setListener();
    }

    private void initData() {
        btn_rtc.setVisibility(View.VISIBLE);
        btn_rtc.setText(R.string.str_send);
        tv_title.setText(R.string.group_chat);
        adAdapter = new ForwardGroupAdapter(ForwardGroupActivity.this, grouplist);
        list.setAdapter(adAdapter);

    }

    private void getData() {
        messageOrigin = getIntent().getParcelableExtra("htMessage");
        grouplist = HTClient.getInstance().groupManager().getAllGroups();
    }

    private void initView() {
        tv_group_check = (TextView) findViewById(R.id.tv_group_check);
        tv_group_check.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        list = (ListView) findViewById(R.id.list);
        btn_rtc = (Button) findViewById(R.id.btn_rtc);
    }

    private void setListener() {
        tv_group_check.setOnClickListener(this);
        list.setOnItemClickListener(this);
        btn_rtc.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.toggle();
        ForwardGroupAdapter.getIsSelected().put(position, checkBox.isChecked());//将CheckBox的选中状况记录下来
        // 调整选定条目
        if (checkBox.isChecked()) {
            checkGroupList.add(adAdapter.getItem(position));
        } else {
            checkGroupList.remove(adAdapter.getItem(position));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_group_check:

                break;
            case R.id.btn_rtc:
                if (checkGroupList.size() == 0 || checkGroupList == null) {
                    CommonUtils.showToastShort(ForwardGroupActivity.this, R.string.please_check_group);
                    return;
                }
                showMessageFarWordDialog(checkGroupList);
                break;
        }
    }

    private void showMessageFarWordDialog(final List<HTGroup> users) {
        CommonUtils.showMessageCopyForwordTipsAlert(ForwardGroupActivity.this, R.string.forword_always, String.format(getString(R.string.forword_group), String.valueOf(users.size())), new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {


                for (int i = 0; i < users.size(); i++) {
                    HTGroup group = users.get(i);
                    HTMessage htMessage = new HTMessage();
                    htMessage.setFrom(UserManager.get().getMyUserId());
                    htMessage.setAttribute("avatar", UserManager.get().getMyAvatar());
                    htMessage.setAttribute("nick", UserManager.get().getMyNick());
                    htMessage.setBody(messageOrigin.getBody());
                    htMessage.setMsgId(UUID.randomUUID().toString());
                    htMessage.setTo(group.getGroupId());
                    htMessage.setDirect(HTMessage.Direct.SEND);
                    htMessage.setLocalTime(System.currentTimeMillis());
                    htMessage.setChatType(ChatType.groupChat);
                    htMessage.setType(messageOrigin.getType());
                    htMessage.setStatus(HTMessage.Status.CREATE);

                    sendMessage(htMessage);
                }
            }

            @Override
            public void onCancleClock() {

            }
        });
    }

    private void sendMessage(final HTMessage htMessage) {
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(final long timeStamp) {
                htMessage.setStatus(HTMessage.Status.SUCCESS);
                htMessage.setTime(timeStamp);
                CommonUtils.upLoadMessage(htMessage);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message", htMessage));

            }

            @Override
            public void onFailure() {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message", htMessage));
            }
        });
        setResult(RESULT_OK);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        grouplist.clear();
        grouplist.addAll(HTClient.getInstance().groupManager().getAllGroups());
        adAdapter.notifyDataSetChanged();
    }


//        for (HTMessage htMessage : msgList) {
//            if (htMessage.getMsgId().equals(msgId)) {
//                return htMessage;
//            }
//        }
//        return null;
//    }
}
