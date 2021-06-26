package com.htmessage.yichat.acitivity.chat.forward;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;


public class ForwardSingleActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> friends = new ArrayList<>();
    private TextView tv_group_check, tv_title;
    private ListView list;
    private ForwardSingleAdapter adAdapter;
    private Button btn_rtc;
    private HTMessage htMessageOrigin;
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
        getContacts();
        adAdapter = new ForwardSingleAdapter(ForwardSingleActivity.this, friends);
        list.setAdapter(adAdapter);
    }

    private void getData() {


        getContacts();

        htMessageOrigin = getIntent().getParcelableExtra("htMessage");
        //转发给个人好友，只需要改变下ext中的用户资料 msgId,发出方from  接收方to即可
//        if(   getIntent().getParcelableExtra("htMessage")!=null){
//            htMessageOrigin = getIntent().getParcelableExtra("htMessage");
//        }


    }

    private void getContacts() {
        friends.clear();
        JSONArray jsonArray=UserManager.get().getMyFrindsJsonArray();

        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            User user=new User(jsonObject);
            friends.add(user);
        }
        // 对list进行排序
        Collections.sort(friends, new PinyinComparator() {
        });
    }

    private void initView() {
        tv_group_check = (TextView) findViewById(R.id.tv_group_check);
        tv_title = (TextView) findViewById(R.id.tv_title);
        list = (ListView) findViewById(R.id.list);
        btn_rtc = (Button) findViewById(R.id.btn_rtc);
    }

    private void setListener() {
        tv_group_check.setOnClickListener(this);
        list.setOnItemClickListener(this);
        btn_rtc.setOnClickListener(this);
        this.findViewById(R.id.tv_all_users).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        ForwardSingleAdapter.getIsSelected().put(position, checkBox.isChecked());//将CheckBox的选中状况记录下来
        // 调整选定条目
        if (checkBox.isChecked()) {
            users.add(adAdapter.getItem(position));
        } else {
            users.remove(adAdapter.getItem(position));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_group_check:
                startActivityForResult(new Intent(ForwardSingleActivity.this, ForwardGroupActivity.class).putExtra("htMessage", htMessageOrigin), 3002);
                break;
            case R.id.btn_rtc:
                if (users.size() == 0 || users == null) {
                    CommonUtils.showToastShort(ForwardSingleActivity.this, R.string.please_check_contant);
                    return;
                }
                showMessageFarWordDialog(users);
                break;

            case R.id.tv_all_users:
                showMessageFarWordDialog(friends);
                break;
        }
    }

    private void showMessageFarWordDialog(final ArrayList<User> users) {
        if(users.size()==0){
            return;
        }
        CommonUtils.showMessageCopyForwordTipsAlert(ForwardSingleActivity.this, R.string.forword_always, String.format(getString(R.string.forword_people), String.valueOf(users.size())), new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {
                CommonUtils.showDialogNumal(ForwardSingleActivity.this,"正在发送");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < users.size(); i++) {
                            User user = users.get(i);
                            String chatTo = user.getUserId();
                             HTMessage htMessage=new HTMessage();
                            htMessage.setFrom(UserManager.get().getMyUserId());
                            htMessage.setAttribute("avatar", UserManager.get().getMyAvatar());
                            htMessage.setAttribute("nick", UserManager.get().getMyNick());
                            htMessage.setBody(htMessageOrigin.getBody());
                            htMessage.setMsgId(UUID.randomUUID().toString());
                            htMessage.setTo(chatTo);
                            htMessage.setDirect(HTMessage.Direct.SEND);
                            htMessage.setLocalTime(System.currentTimeMillis());
                            htMessage.setChatType(ChatType.singleChat);
                            htMessage.setType(htMessageOrigin.getType());
                            htMessage.setStatus(HTMessage.Status.CREATE);
                            if(handler==null){
                                break;
                            }

                            Message message=handler.obtainMessage();
                            message.obj=htMessage;
                            message.what=1000;
                            if(i== users.size()-1){
                                message.arg1=1;
                            }
                            handler.sendMessageDelayed(message,i*100);

//        htMessageSingle.setChatType(ChatType.singleChat);
//        htMessageSingle.setTo();


                        }
                    }
                }).start();

            }

            @Override
            public void onCancleClock() {

            }
        });
    }

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 1000:
                    HTMessage htMessage= (HTMessage) msg.obj;
                    int stop=msg.arg1;

                    ForwardSingleActivity.this.sendMessage(htMessage);
                    if(stop==1){
                        CommonUtils.cencelDialog();
                        finish();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;
        CommonUtils.cencelDialog();
    }

    private void sendMessage(final HTMessage htMessage) {
         HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
            }

            @Override
            public void onSuccess(final long timeStamp) {
                LocalBroadcastManager.getInstance(ForwardSingleActivity.this).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

//                htMessage.setStatus(HTMessage.Status.SUCCESS);
//                htMessage.setTime(timeStamp);
//                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
//
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message", htMessage));

            }

            @Override
            public void onFailure() {
//
//                htMessage.setStatus(HTMessage.Status.FAIL);
//                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message", htMessage));

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3002:
                if (resultCode == RESULT_OK) {
                    ForwardSingleActivity.this.finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        friends.clear();
        getContacts();
        if (adAdapter != null) {
            adAdapter.notifyDataSetChanged();
        }
    }


    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }
        }
    }

}
