package com.htmessage.fanxinht.acitivity.chat.forward;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat
 * 类描述：CheckGroupActivity 描述: 选择转发群组
 * 创建人：songlijie
 * 创建时间：2017/3/18 17:04
 * 邮箱:814326663@qq.com
 */
public class ForwardGroupActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected List<HTGroup> grouplist = new ArrayList<>();
    protected List<HTGroup> checkGroupList = new ArrayList<>();
    private TextView tv_group_check,tv_title;
    private ListView list;
    private ForwardGroupAdapter adAdapter;
    private String forwordType;
    private String localUrl,obj,imagePath,msgId,toChatUsername,exobj;
    private HTMessage message1;
    private JSONObject object,extJSON;
    private Button btn_rtc;
    private List<HTMessage> msgList;

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
        msgList = HTClient.getInstance().messageManager().getMessageList(toChatUsername);
        extJSON = JSONObject.parseObject(exobj);
        message1 = getCopyMessage(msgId);
        tv_title.setText(R.string.group_chat);
        adAdapter = new ForwardGroupAdapter(ForwardGroupActivity.this, grouplist);
        list.setAdapter(adAdapter);

    }

    private void getData() {
        obj = getIntent().getStringExtra("obj");
        object = JSONObject.parseObject(obj);
        imagePath = object.getString("imagePath");
        forwordType = object.getString("forwordType");
        localUrl = object.getString("localPath");
        msgId = object.getString("msgId");
        toChatUsername =object.getString("toChatUsername");
        exobj =object.getString("exobj");
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
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        ForwardGroupAdapter.getIsSelected().put(position, checkBox.isChecked());//将CheckBox的选中状况记录下来
        // 调整选定条目
        if (checkBox.isChecked() == true) {
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
                    Toast.makeText(ForwardGroupActivity.this, R.string.please_check_group, Toast.LENGTH_SHORT).show();
                    return;
                }
                showMessageFarWordDialog(checkGroupList, forwordType, localUrl);
                break;
        }
    }

    private void showMessageFarWordDialog(final List<HTGroup> users, final String forwordType, final String localUrl) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.item_dialog_gridview, null);
        TextView tv_forward = (TextView) view.findViewById(R.id.tv_forward);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        textView.setText(R.string.forword_always);
        textView.setText(R.string.forword_always);
        if ("image".equals(forwordType) && localUrl != null) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
        tv_forward.setText(getString(R.string.forword_group).replace("1", String.valueOf(users.size())));
        buidler.setView(view);
        final AlertDialog dialog = buidler.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for (int i = 0; i < users.size(); i++) {
                    HTGroup easeUser = users.get(i);
                    switch (forwordType) {
                        case "file":
                            HTMessageFileBody fileBody = (HTMessageFileBody) message1.getBody();
                            HTMessage emMessage = HTMessage.createFileSendMessage(easeUser.getGroupId(), localUrl, fileBody.getSize());
                            sendMessage(emMessage);
                            break;
                        case "text":
                            HTMessage textMessage= HTMessage.createTextSendMessage(easeUser.getGroupId(),localUrl);
                            sendMessage(textMessage);
                            break;
                        case "video":
                            HTMessageVideoBody videoBody = (HTMessageVideoBody) message1.getBody();
                            HTMessage emvideoMessage = HTMessage.createVideoSendMessage(easeUser.getGroupId(), localUrl, videoBody.getLocalPathThumbnail(), videoBody.getVideoDuration());
                            sendMessage(emvideoMessage);
                            break;
                        case "voice":
                            HTMessageVoiceBody voiceBody = (HTMessageVoiceBody) message1.getBody();
                            HTMessage voiceMSg = HTMessage.createVoiceSendMessage(easeUser.getGroupId(), localUrl, voiceBody.getAudioDuration());
                            sendMessage(voiceMSg);
                            break;
                        case "image":
                            HTMessageImageBody imageBody = (HTMessageImageBody) message1.getBody();
                            HTMessage message = HTMessage.createImageSendMessage(easeUser.getGroupId(), localUrl, imageBody.getSize());
                            sendMessage(message);
                            break;
                        case "location":
                            HTMessageLocationBody locationBody = (HTMessageLocationBody) message1.getBody();
                            HTMessage locationSendMessage = HTMessage.createLocationSendMessage(easeUser.getGroupId(), locationBody.getLatitude(), locationBody.getLongitude(), locationBody.getAddress(), localUrl);
                            sendMessage(locationSendMessage);
                            break;
                    }
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendMessage(final HTMessage htMessage){
        htMessage.setAttributes(extJSON.toJSONString());
        htMessage.setChatType(ChatType.groupChat);
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htMessage.setStatus(HTMessage.Status.SUCCESS);
                        HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message",htMessage));
                    }
                });

            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htMessage.setStatus(HTMessage.Status.FAIL);
                        HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message",htMessage));
                    }
                });
            }
        });
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

    private HTMessage getCopyMessage(String msgId) {
        for (HTMessage htMessage : msgList) {
            if (htMessage.getMsgId().equals(msgId)) {
                return htMessage;
            }
        }
        return null;
    }
}
