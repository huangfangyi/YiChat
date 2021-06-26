package com.htmessage.yichat.acitivity.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.forward.ForwardSingleActivity;
import com.htmessage.yichat.acitivity.chat.weight.ChatInputView;
import com.htmessage.yichat.acitivity.chat.weight.emojicon.Emojicon;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.acitivity.red.RedDetailActivity;
import com.htmessage.yichat.acitivity.red.dialog.DialogOpenActivity;
import com.htmessage.yichat.acitivity.redpacket.SendSingleRPActivity;
import com.htmessage.yichat.acitivity.showbigimage.ShowBigImageActivity;
import com.htmessage.yichat.runtimepermissions.PermissionsManager;
import com.htmessage.yichat.runtimepermissions.PermissionsResultAction;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.TipsAlertDialog;
import com.htmessage.yichat.widget.VoiceRecorderView;
import com.htmessage.yichat.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by huangfangyi on 2017/7/18.
 * qq 84543217
 */

public class ChatFragment extends Fragment implements ChatContract.View, SwipyRefreshLayout.OnRefreshListener {
    private ChatContract.Presenter presenter;
    private ChatAdapter recyclerViewAdapter;
    private ChatInputView chatInputView;
    private VoiceRecorderView voiceRecorderView;
    private SwipyRefreshLayout refreshlayout;
    private RecyclerView listView;
    private int chatType;
    private String toChatUsername;
    private MyBroadcastReciver myBroadcastReciver;
    private static int[] itemNamesSingle = {R.string.attach_picture, R.string.attach_take_pic,R.string.attach_red};//
    private static int[] itemIconsSingle = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector,R.drawable.type_redpacket};//
    private static int[] itemNamesGroup = {R.string.attach_picture, R.string.attach_take_pic,R.string.attach_red,};//
    private static int[] itemIconsGroup = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector,R.drawable.type_redpacket,};//,R.drawable.type_redpacket
    //RecyclerView 的滚动状态
    private int RVScrollState = 0;//0是停止滚动，1是开始滚动，2正在滑动

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatType = getArguments().getInt("chatType", MessageUtils.CHAT_SINGLE);
        toChatUsername = getArguments().getString("userId");
        ChatFileManager.get().clearImageOrVideoMessage();
//        if(chatType==2){
//            JSONObject data=MmvkManger.getIntance().getJSON(toChatUsername+"_groupInfo_cache");
//            if(data!=null){
//                GroupInfoManager.getInstance().saveGroupInfoTemp(data);
//            }
//        }


        initView();
        setListener();
        initData();
    }


    private void initView() {
        voiceRecorderView = (VoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        refreshlayout = (SwipyRefreshLayout) getView().findViewById(R.id.refreshlayout);
        listView = (RecyclerView) getView().findViewById(R.id.list);
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        chatInputView = (ChatInputView) getView().findViewById(R.id.inputView);
        if (chatType == MessageUtils.CHAT_SINGLE) {
            chatInputView.initView(getActivity(), refreshlayout, itemNamesSingle, itemIconsSingle);
        } else {
            LoggerUtils.e("toChatUsername---" + toChatUsername);
//            if (GroupInfoManager.getInstance().isManager(toChatUsername)) {
//                int[] itemNamesGroup = {R.string.attach_picture, R.string.attach_take_pic};//, R.string.attach_red, R.string.attach_video_call
//                int[] itemIconsGroup = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector, R.drawable.type_redpacket, R.drawable.icon_zhen};
//
//
//                chatInputView.initView(getActivity(), refreshlayout, itemNamesGroup, itemIconsGroup);
//            } else {
//
//                int[] itemNamesGroup = {R.string.attach_picture, R.string.attach_take_pic, R.string.attach_red};//, R.string.attach_red, R.string.attach_video_call
//                int[] itemIconsGroup = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector, R.drawable.type_redpacket};
//                chatInputView.initView(getActivity(), refreshlayout, itemNamesGroup, itemIconsGroup);
//            }
            chatInputView.initView(getActivity(), refreshlayout, itemNamesGroup, itemIconsGroup);
            checkSilent();

        }

    }

    private void checkSilent() {
        if (GroupInfoManager.getInstance().isGroupSilent(toChatUsername) && !GroupInfoManager.getInstance().isManager(toChatUsername)) {
            chatInputView.getEditText().setText("");
            chatInputView.getEditText().setHint("群已被禁言");
            chatInputView.getEditText().clearFocus();
            chatInputView.getEditText().setInputType(InputType.TYPE_NULL);

        } else {
            chatInputView.getEditText().setHint("");
            chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        if (chatType == MessageUtils.CHAT_GROUP) {
            chatInputView.getEditText().addTextChangedListener(new TextWatcher() {
                boolean isDelAt = false;
                int strLength = 0;
                int indexAt = 0;
                String nick;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s != null && s.toString().endsWith(" ") && s.toString().contains("@")) {
                        //字符串不为空，并且以空格结尾，此时判断空格和@之间是否是一个@用户
                        indexAt = s.toString().lastIndexOf("@")+1;
                        nick= s.toString().substring(indexAt, s.length()-1);
                        Log.d("nick---", nick);
                        if (presenter.isHasAtNick(nick)) {
                            isDelAt = true;
                            strLength = s.length();
                            Log.d("nick---7", strLength+"");
                            Log.d("nick---8", isDelAt+"");
                            return;
                        }
                    }
                    isDelAt = false;
                    strLength = 0;
                    indexAt = 0;

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        presenter.startChooseAtUser();
                    }

//                    else if(isDelAt&&s!=null&&s.length()<strLength){
//
//                        chatInputView.getEditText().dispatchKeyEvent(new KeyEvent(
//                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
//
//                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("nick---1", s.length()+"");
                     Log.d("nick---3", strLength+"");
                    if (isDelAt && s.length()!= 0 && s.length() < strLength) {
                        chatInputView.getEditText().setText(s.subSequence(0, indexAt-1));
                        chatInputView.getEditText().setSelection(chatInputView.getEditText().getText().length());
                        presenter.deleteAtUser(nick);
                    }
                }
            });

//            chatInputView.getEditText().setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_DEL) {
//                    }
//                    return false;
//                }
//            });


        }

        refreshlayout.setOnRefreshListener(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (chatInputView.isShown()) {
                    chatInputView.hideEmotionLayout(false);
                    chatInputView.hideSoftInput();
                }
                return false;
            }
        });

        //手机输入法键盘 Enter键直接发消息
        chatInputView.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String trim = chatInputView.getEditText().getText().toString().trim();
                    chatInputView.getEditText().getText().clear();
                    if (!TextUtils.isEmpty(trim)) {
                        presenter.sendTextMessage(trim);
                    }
                    return true;
                }
                return false;
            }
        });
        chatInputView.setInputViewLisenter(new MyInputViewLisenter());
        //解决swipelayout与Recyclerview的冲突
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshlayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RVScrollState = newState;


            }

        });


//        if (chatType == MessageUtils.CHAT_GROUP) {
//            chatInputView.getEditText().addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (count == 1 && HTConstant.ATHOLDER.equals(String.valueOf(s.charAt(start)))) {
//                        presenter.sendAtMessage();
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//        }

    }


    private void initData() {
        Bundle fragmentArgs = getArguments();
        if (fragmentArgs == null || presenter == null) {
            getActivity().finish();
            return;
        }

        presenter.initData(fragmentArgs);
        myBroadcastReciver = new MyBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_FORWORD);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_EMPTY);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        intentFilter.addAction(IMAction.ACTION_REMOVED_FROM_GROUP);
        intentFilter.addAction(IMAction.ACTION_DELETE_GROUP);
        intentFilter.addAction(IMAction.ACTION_HAS_CANCLED_NO_TALK);
        intentFilter.addAction(IMAction.ACTION_HAS_NO_TALK);
        intentFilter.addAction(IMAction.ACTION_UPDATE_CHAT_TITLE);
        intentFilter.addAction(IMAction.ACTION_SET_OR_CANCLE_GROUP_MANAGER);
        intentFilter.addAction(IMAction.RED_PACKET_HAS_GOT);
        //群有新公告
        intentFilter.addAction(IMAction.NEW_GROUP_NOTICE);
        intentFilter.addAction(IMAction.XMPP_LOGIN_OR_RELOGIN);

        //被单个禁言
        intentFilter.addAction(IMAction.NO_TALK_USER);

        //被解除单个禁言
        intentFilter.addAction(IMAction.NO_TALK_USER_CANCEL);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReciver, intentFilter);


    }


    @Override
    public void onRefresh(int index) {
        refreshlayout.setRefreshing(false);
        presenter.loadMoreMessages();
    }

    @Override
    public void onLoad(int index) {

    }

    private class MyInputViewLisenter implements ChatInputView.InputViewLisenter {

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new VoiceRecorderView.EaseVoiceRecorderCallback() {

                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    presenter.sendVoiceMessage(voiceFilePath, voiceTimeLength);
                }
            });
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {

        }

        @Override
        public void onSendButtonClicked(String content) {
            presenter.sendTextMessage(content);
        }

        @Override
        public boolean onEditTextLongClick() {
//            String myCopy = MmvkManger.getIntance().getAsString("myCopy");
//            if (!TextUtils.isEmpty(myCopy)) {
//                JSONObject jsonObject = JSONObject.parseObject(myCopy);
//                String msgId = jsonObject.getString("msgId");
//                String imagePath = jsonObject.getString("imagePath");
//                HTMessage emMessage = presenter.getMessageById(msgId);
//                if (emMessage == null) {
//                    return true;
//                }
//                showCopyContent(jsonObject.getString("copyType"), jsonObject.getString("localPath"), emMessage, imagePath);
//                return true;
//            }
            return false;
        }

        @Override
        public void onEditTextUp() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToBottom();
                }
            }, 100);
        }


        @Override
        public void onAlbumItemClicked() {


            if (!PermissionsManager.getInstance().hasPermission(getBaseContext(), Manifest.permission.CAMERA) ||
                    !PermissionsManager.getInstance().hasPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    !PermissionsManager.getInstance().hasPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                CommonUtils.showToastShort(getActivity(), R.string.miss_permission_storage);
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
//                requestPermissions(, 100);
                return;
            }
//            if (
//                    || !CommonUtils.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                   || !CommonUtils.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                CommonUtils.showToastShort(getActivity(), R.string.miss_permission_storage);
//
//                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//                return;
//            }
            presenter.selectPicFromCamera(getActivity());
        }

        @Override
        public void onPhotoItemClicked() {
            if (!PermissionsManager.getInstance().hasPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    !PermissionsManager.getInstance().hasPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                CommonUtils.showToastShort(getActivity(), R.string.miss_permission_camera);
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
//                requestPermissions(, 100);
                return;
            }
            presenter.selectPicFromLocal(getActivity());
        }

        @Override
        public void onLocationItemClicked() {
            if (chatType == 2) {

                CommonUtils.showInputDialog(getContext(), "您确定要震群内所有人吗？", "请输入内容，或者点击确定直接震", "震所有群友上线", new CommonUtils.DialogClickListener() {
                    @Override
                    public void onCancleClock() {

                    }

                    @Override
                    public void onPriformClock(String msg) {
                        sendCMDandHTmsg(msg);
                    }
                });
            } else {

                presenter.startCardSend(getActivity());
            }
        }

        @Override
        public void onVideoItemClicked() {
//            if (!CommonUtils.checkPermission(getActivity(), Manifest.permission.CAMERA)
//                    && !CommonUtils.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                    && !CommonUtils.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                CommonUtils.showToastShort(getActivity(), R.string.miss_permission_camera);
//                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//                return;
//            }
//            presenter.selectVideo();
        }

        @Override
        public void onCallItemClicked() {

        }

        @Override
        public void onFileItemClicked() {


        }

        @Override
        public void onRedPacketItemClicked() {
            getActivity().startActivityForResult(new Intent(getActivity(), SendSingleRPActivity.class).putExtra("chatType", chatType).putExtra("userId", toChatUsername), ChatPresenter.REQUEST_CODE_SELECT_RP);
        }

        @Override
        public void onTransferItemClicked() {


        }

        @Override
        public void onContentCardClicked() {

        }

        @Override
        public void onMoreButtonClick() {
        }

        @Override
        public void onXunZhangonClick() {
        }

        @Override
        public void onBigEmojiClick(String content) {

            presenter.sendTextMessage(content);
        }
    }


    private void sendCMDandHTmsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = "震所有群友上线";
        }

        JSONObject body = new JSONObject();
        body.put("action", 40001);

        JSONObject data = new JSONObject();
        data.put("groupId", toChatUsername);
        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
        if (htGroup == null) {
            return;
        }
        data.put("groupName", htGroup.getGroupName());
        data.put("nick", UserManager.get().getMyNick());
        data.put("avatar", UserManager.get().getMyAvatar());
        data.put("content", msg);
        body.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(toChatUsername);
        customMessage.setBody(body.toJSONString());
        customMessage.setChatType(ChatType.groupChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });


        presenter.sendTextMessage(msg);

    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }


    @Override
    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }


    private void scrollToBottom() {

        if (RVScrollState == 0 && recyclerViewAdapter.getItemCount() > 0) {
            listView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);
        }


    }

    @Override
    public void insertRecyclerView(int position, int count, int type) {
        recyclerViewAdapter.notifyItemRangeInserted(position, count);
        if (type == 1) {
            //收到新消息
            LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
            int visibleposition = layoutManager.findLastCompletelyVisibleItemPosition();
            if ((recyclerViewAdapter.getItemCount() - visibleposition) < 5) {
                scrollToBottom();
            }
        } else if (type == 2) {
            //发出一条新消息
            scrollToBottom();
        }


    }


    @Override
    public void loadMoreMessageRefresh(int position, int count) {
        recyclerViewAdapter.notifyItemRangeInserted(position, count);
        LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
        if (count > 0) {
            layoutManager.scrollToPositionWithOffset(count - 1, 80);
        }


    }

    @Override
    public void initRecyclerView(List<HTMessage> messageList) {
        recyclerViewAdapter = new ChatAdapter(messageList, getActivity(), toChatUsername, chatType);
        listView.setAdapter(recyclerViewAdapter);
        scrollToBottom();
        recyclerViewAdapter.setOnResendViewClick(new ChatAdapter.OnResendViewClick() {

            @Override
            public void resendMessage(HTMessage htMessage) {
                showReSendDialog(htMessage);
            }

            @Override
            public void onRedMessageClicked(HTMessage htMessage, String evnId) {
                presenter.onOpenRedpacket(htMessage, evnId);
            }

            @Override
            public void onTransferMessageClicked(final JSONObject jsonObject, String transferId) {

            }

            @Override
            public void onAvatarLongClick(String userId) {
                if (chatType == MessageUtils.CHAT_GROUP && !HTApp.getInstance().getUsername().equals(toChatUsername)) {

                    if (presenter.isHasAt(userId)) {
                        return;
                    }
                    String realNick = UserManager.get().getUserRealNick(userId);
                    setAtUserStytle(realNick, false);
                    presenter.setAtUser(realNick, userId);

                }
            }

            @Override
            public void onItemLongClick(HTMessage htMessage, int position) {
                if (htMessage != null) {
                    if (htMessage.getType() == HTMessage.Type.TEXT) {
                        int action = htMessage.getIntAttribute("action", 0);
                        if (action == 10005 || action == 10004) {
                            return;
                        }
//                        if (action == 10001 || action == 10002 || action == 10007 || action == 20000 || action == 20001) {
//                            //
//                            showCardMsgDialog(htMessage);
//                            return;
//                        }
                    }

                    showMsgDialog(htMessage, position, chatType);

                }
            }

            @Override
            public void onItemClick(HTMessage htMessage, int position) {

            }

            @Override
            public void onAvatarClick(String userId) {
                if (userId.equals(UserManager.get().getMyUserId())) {
                    return;
                }
                if (chatType == 2) {
                    if (GroupInfoManager.getInstance().isManager(toChatUsername)) {

                        new HTAlertDialog(getActivity(), null, new String[]{"查看资料", "禁言"}).init(new HTAlertDialog.OnItemClickListner() {
                            @Override
                            public void onClick(int position) {
                                switch (position) {
                                    case 0:
                                        startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId", userId));

                                        break;
                                    case 1:
                                        GroupInfoManager.getInstance().addSilentUsers(toChatUsername, userId);
                                        break;
                                }


                            }
                        });
                    }
                    return;
                }
                startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId", userId));

            }

            @Override
            public void onImageMessageClick(HTMessage htMessage) {
//                List<HTMessage> htMessageList=new ArrayList<>();
//                htMessageList.add(htMessage);
                int indexPage = ChatFileManager.get().getImageOrVideoMessage().indexOf(htMessage);
                Intent intent = new Intent(getActivity(), ShowBigImageActivity.class);
                intent.putExtra("indexPage", indexPage);
                startActivity(intent);
            }

            @Override
            public void onVideoMessageClick(HTMessage htMessage) {
                int indexPage = ChatFileManager.get().getImageOrVideoMessage().indexOf(htMessage);
                Intent intent = new Intent(getActivity(), ShowBigImageActivity.class);
                intent.putExtra("indexPage", indexPage);

                startActivity(intent);


            }
        });

    }

    public void setAtUserStytle(String atUserNick, boolean isFromChooseList) {
        //isFromChooseList是输入@号进行选择的
        String originContent = chatInputView.getEditText().getText().toString();
        String content = "@" + atUserNick + " ";
        if (isFromChooseList) {
            content = atUserNick + " ";
        }


        chatInputView.getEditText().setText(originContent + content);
        chatInputView.getEditText().setSelection((originContent + content).length());
    }

    @Override
    public void updateRecyclerView(int position) {
        recyclerViewAdapter.notifyItemChanged(position);
    }


    @Override
    public void deleteItemRecyclerView(int position) {
        recyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyClear() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void startToDialogRP(JSONObject jsonObject) {
        startActivity(new Intent(getActivity(), DialogOpenActivity.class)
                .putExtra("chatType", chatType)
                .putExtra("chatTo", toChatUsername)
                .putExtra("data", jsonObject.toJSONString())
        );
    }

    @Override
    public void startToDetailRp(JSONObject jsonObject) {
        startActivity(new Intent(getActivity(), RedDetailActivity.class)
                .putExtra("chatType", chatType)
                .putExtra("chatTo", toChatUsername)
                .putExtra("data", jsonObject.toJSONString())
        );
    }

    @Override
    public void onGroupInfoLoaded() {
        if (GroupInfoManager.getInstance().isGroupSilent(toChatUsername)) {
            chatInputView.getEditText().setText("");
            chatInputView.getEditText().setHint("群已被禁言");
            chatInputView.getEditText().clearFocus();
            chatInputView.getEditText().setInputType(InputType.TYPE_NULL);

        } else {
            chatInputView.getEditText().setHint("");
            chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        }

//        if (GroupInfoManager.getInstance().isManager(toChatUsername)) {
//            int[] itemNamesGroup = {R.string.attach_picture, R.string.attach_take_pic, R.string.attach_red, R.string.attach_zhen};//, R.string.attach_red, R.string.attach_video_call
//            int[] itemIconsGroup = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector, R.drawable.type_redpacket, R.drawable.icon_zhen};
//
//
//            chatInputView.initView(getActivity(), refreshlayout, itemNamesGroup, itemIconsGroup);
//        } else {
//
//            int[] itemNamesGroup = {R.string.attach_picture, R.string.attach_take_pic, R.string.attach_red};//, R.string.attach_red, R.string.attach_video_call
//            int[] itemIconsGroup = {R.drawable.chat_image_selector, R.drawable.chat_takepic_selector, R.drawable.type_redpacket};
//            chatInputView.initView(getActivity(), refreshlayout, itemNamesGroup, itemIconsGroup);
//        }
    }


    @Override
    public void onDestroy() {

        if (myBroadcastReciver != null && getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myBroadcastReciver);
        }
        ChatFileManager.get().clearImageOrVideoMessage();
        super.onDestroy();
    }

    private void showMsgDialog(final HTMessage message, final int postion, final int chatType) {
        List<String> items = new ArrayList<>();
        if (chatType == 1) {
            //单聊有删除，群聊由于存服务器端，无删除选项
            items.add("删除");
        }

        if (message.getType() == HTMessage.Type.TEXT) {
            //文字消息有复制，其他都是转发
            items.add("复制");
        }
        items.add("转发");
        if (message.getDirect() == HTMessage.Direct.SEND || (chatType == 2 && GroupInfoManager.getInstance().isManager(toChatUsername))) {
            //发送者和群聊管理员都有撤回权限
            items.add("撤回");
        }
        if (message.getType() == HTMessage.Type.VOICE) {
            if (SettingsManager.getInstance().getSettingMsgSpeaker()) {
                items.add("听筒播放");
            } else {
                items.add("扬声器播放");
            }

        }
        int action = message.getIntAttribute("action", 0);
        if (action == 10001) {
            if (!Constant.isRedpacketCanWithdraw) {
                return;
            }
            items.remove("转发");
            items.remove("复制");
            if (chatType == 2) {
                items.remove("删除");
            }


        }

        HTAlertDialog dialog = new HTAlertDialog(getActivity(), null, items);
        dialog.setOnItemClickListner(new HTAlertDialog.OnItem2ClickListner() {
            @Override
            public void onClick(String string) {
                if ("删除".equals(string)) {
                    presenter.deleteSingChatMessage(message);
                } else if ("复制".equals(string)) {

                    copyToClipboard(getActivity(), ((HTMessageTextBody) message.getBody()).getContent());

                    // presenter.copyMessage(message);

                } else if ("转发".equals(string)) {

                    forwordMessage(message);

                    // presenter.forwordMessage(message);
                } else if ("撤回".equals(string)) {
                    if ((chatType == 2 && GroupInfoManager.getInstance().isManager(toChatUsername)) || message.getFrom().equals(UserManager.get().getMyUserId())) {
                        //群聊消息+我是管理员+ 是自己的
                        presenter.withdrowMessage(message, postion);
                    }

                } else if ("听筒播放".equals(string)) {

                    SettingsManager.getInstance().setSettingMsgSpeaker(false);

                } else if ("扬声器播放".equals(string)) {
                    SettingsManager.getInstance().setSettingMsgSpeaker(true);
                }
            }


        });

    }


//    /**
//     * 打开红包
//     *
//     * @param
//     * @param envId
//     */
//
//
//    private void openRedMessage(final HTMessage message, String envId) {
//        if (message.getDirect() == HTMessage.Direct.SEND && chatType != MessageUtils.CHAT_GROUP) {
//            Intent intent = new Intent(getActivity(), RedDetailActivity.class);
//            String toUser;
//            if (message.getDirect() == HTMessage.Direct.RECEIVE) {
//                toUser = HTApp.getInstance().getUsername();
//
//            } else {
//                toUser = toChatUsername;
//
//            }
//            intent.putExtra("envId", envId);
//            intent.putExtra("chatType", chatType);
//            intent.putExtra("message", message);
//            intent.putExtra("toUser", toUser);
//            startActivity(intent);
//        } else {
//            lookRedDetails(envId, message, false);
//        }
//    }


    private void copyToClipboard(Context context, CharSequence content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);

//        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        if (clipboard != null) {
//            clipboard.setPrimaryClip(ClipData.newPlainText("复制消息", content));//参数一：标签，可为空，参数二：要复制到剪贴板的文本
////        //获取剪贴板管理器：
//ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//// 创建普通字符型ClipData
//ClipData mClipData = ClipData.newPlainText("Label", "这里是要复制的文字");
//// 将ClipData内容放到系统剪贴板里。
//cm.setPrimaryClip(mClipData);
//
//            if (clipboard.hasPrimaryClip()) {
//                clipboard.getPrimaryClip().getItemAt(0).getText();
//            }
        //   }
    }

    private void forwordMessage(HTMessage htMessage) {
        startActivity(new Intent(getActivity(), ForwardSingleActivity.class).putExtra("htMessage", htMessage));
    }


    /**
     * 重新发送消息
     *
     * @param htMessage
     */
    private void showReSendDialog(final HTMessage htMessage) {
        TipsAlertDialog alertDialog = new TipsAlertDialog(getActivity());
        alertDialog.setTipsTitle(R.string.prompt);
        alertDialog.setTipsContent(R.string.resend_text);
        alertDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                presenter.resendMessage(htMessage);
            }

            @Override
            public void onTipsCancleClick() {

            }
        });
        alertDialog.showTips();
    }

    public void onBackPressed() {
        if (!chatInputView.interceptBackPress()) {
            getActivity().finish();

        }
    }


    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IMAction.ACTION_MESSAGE_WITHDROW)) {
                String chatTo = intent.getStringExtra("chatTo");
                if (toChatUsername.equals(chatTo)) {
                    HTMessage htMessage = intent.getParcelableExtra("message");
                    presenter.onMessageWithdrow(htMessage);
                }

            } else if (intent.getAction().equals(IMAction.ACTION_MESSAGE_FORWORD)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onMeesageForward(message);
            } else if (intent.getAction().equals(IMAction.ACTION_NEW_MESSAGE)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onNewMessage(message);
            } else if (IMAction.ACTION_MESSAGE_EMPTY.equals(intent.getAction())) {
                String id = intent.getStringExtra("id");
                if (toChatUsername.equals(id)) {
                    presenter.onMessageClear();
                }
            } else if (IMAction.CMD_DELETE_FRIEND.equals(intent.getAction())) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_USERID);
                if (getActivity() != null) {
                    if (userId.equals(toChatUsername)) {
                        CommonUtils.showToastShort(getActivity(), getString(R.string.just_delete_friend));

                        getActivity().finish();
                    }
                }
            } else if (IMAction.ACTION_REMOVED_FROM_GROUP.equals(intent.getAction())) {
                String userId = intent.getStringExtra("userId");
                if (getActivity() != null) {
                    if (userId.equals(toChatUsername)) {
                        CommonUtils.showToastShort(getActivity(), getString(R.string.just_delete_group));
                        getActivity().finish();
                    }
                }
            } else if (IMAction.ACTION_DELETE_GROUP.equals(intent.getAction())) {
                String groupId = intent.getStringExtra("userId");
                if (getActivity() != null) {
                    if (groupId.equals(toChatUsername)) {
                        getActivity().finish();
                    }
                }
            } else if (IMAction.ACTION_HAS_CANCLED_NO_TALK.equals(intent.getAction())) {//解除禁言
                String userId = intent.getStringExtra("userId");
                String content = intent.getStringExtra("content");

                if (toChatUsername.equals(userId) && !GroupInfoManager.getInstance().isGroupSilent(toChatUsername)) {
                    chatInputView.getEditText().setHint("");
                    chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                }
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
            } else if (IMAction.ACTION_HAS_NO_TALK.equals(intent.getAction())) {//被禁言
                String userId = intent.getStringExtra("userId");
                String content = intent.getStringExtra("content");

                if (toChatUsername.equals(userId)) {
                    Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
                    if (!GroupInfoManager.getInstance().isManager(toChatUsername)) {
                        chatInputView.getEditText().setText("");
                        chatInputView.getEditText().setHint("已被管理员禁言");
                        chatInputView.getEditText().clearFocus();
                        chatInputView.getEditText().setInputType(InputType.TYPE_NULL);
                    }


                }

            } else if (IMAction.ACTION_UPDATE_CHAT_TITLE.equals(intent.getAction())) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_USERID);
                String userNick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                if (userId.equals(toChatUsername) && getActivity() != null) {
                    ((ChatActivity) getActivity()).setTitle(userNick);
                }

            } else if (IMAction.ACTION_SET_OR_CANCLE_GROUP_MANAGER.equals(intent.getAction())) {
                String groupId = intent.getStringExtra("groupId");
                String userId = intent.getStringExtra("userId");
                if (toChatUsername.equals(groupId) && UserManager.get().getMyUserId().equals(userId)) {
                    int action = intent.getIntExtra("action", 0);
                    if (action == 30002) {
                        Toast.makeText(getActivity(), "群主设置你为管理员", Toast.LENGTH_SHORT).show();
                        checkSilent();


                    } else if (action == 30003) {
                        Toast.makeText(getActivity(), "群主取消了你的管理员身份", Toast.LENGTH_SHORT).show();
                        checkSilent();
                    }

                }

            } else if (IMAction.RED_PACKET_HAS_GOT.equals(intent.getAction())) {
                String msgId = intent.getStringExtra("msgId");
                String whiosRP = intent.getStringExtra("whiosRP");
                presenter.sendRedCmdMessage(whiosRP, msgId);
            } else if (IMAction.NEW_GROUP_NOTICE.equals(intent.getAction())) {
                String groupId = intent.getStringExtra("groupId");

                if (!toChatUsername.equals(groupId)) {
                    return;
                }
                String content = intent.getStringExtra("content");
                String title = intent.getStringExtra("title");
                String id = intent.getStringExtra("id");
                String preId = MmvkManger.getIntance().getAsString("group_notice" + HTApp.getInstance().getUsername() + groupId);
                if (!TextUtils.isEmpty(id) && !id.equals(preId)) {
                    showNewNoticeDialog(title, content, id);
                }
            } else if (IMAction.XMPP_LOGIN_OR_RELOGIN.equals(intent.getAction())) {
                if (chatType == 2) {
                    presenter.getGroupInfoInServer(toChatUsername);
                }

            } else if (IMAction.NO_TALK_USER.equals(intent.getAction())) {
                String userId = intent.getStringExtra("userId");
                String content = intent.getStringExtra("content");
                if (toChatUsername.equals(userId)) {
                    CommonUtils.showToastShort(context, content);
                    if (!GroupInfoManager.getInstance().isManager(toChatUsername)) {
                        chatInputView.getEditText().setText("");
                        chatInputView.getEditText().setHint("群已被禁言");
                        chatInputView.getEditText().clearFocus();
                        chatInputView.getEditText().setInputType(InputType.TYPE_NULL);

                    }
                }
            } else if (IMAction.NO_TALK_USER_CANCEL.equals(intent.getAction())) {
                String userId = intent.getStringExtra("userId");
                String content = intent.getStringExtra("content");
                if (toChatUsername.equals(userId)) {
                    CommonUtils.showToastShort(context, content);


                    if (!GroupInfoManager.getInstance().isGroupSilent(toChatUsername)) {
                        chatInputView.getEditText().setHint("");
                        chatInputView.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                    }


                }
            }


        }
    }

    @Override
    public void showNewNoticeDialog(String title, String content, String id) {
        CommonUtils.showAlertDialog(getActivity(), title, content, new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {


            }

            @Override
            public void onCancleClock() {

            }
        });
        MmvkManger.getIntance().putString("group_notice" + HTApp.getInstance().getUsername() + toChatUsername, id);
    }

}
