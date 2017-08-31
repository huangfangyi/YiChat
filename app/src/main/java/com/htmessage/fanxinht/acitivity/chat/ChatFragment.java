package com.htmessage.fanxinht.acitivity.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.weight.ChatInputView;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.Emojicon;
import com.htmessage.fanxinht.acitivity.chat.weight.header.PullToLoadMoreListView;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.widget.HTAlertDialog;
import com.htmessage.fanxinht.widget.VoiceRecorderView;

/**
 * Created by huangfangyi on 2017/7/18.
 * qq 84543217
 */

public class ChatFragment extends Fragment implements ChatContract.View {
    private ChatContract.Presenter presenter;
    private ChatAdapter adapter;
    private PullToLoadMoreListView pullToLoadMoreListView;
    private ChatInputView chatInputView;
    private VoiceRecorderView voiceRecorderView;
    private ListView listView;
    private int chatType;
    private String toChatUsername;
    private MyBroadcastReciver myBroadcastReciver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        voiceRecorderView = (VoiceRecorderView) root.findViewById(R.id.voice_recorder);
        pullToLoadMoreListView = (PullToLoadMoreListView) root.findViewById(R.id.list);
        pullToLoadMoreListView.setOnRefreshListener(new PullToLoadMoreListView.OnRefreshListener() {

            @Override
            public void onPullDownLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.loadMoreMessages();
                        pullToLoadMoreListView.onRefreshComplete();

                    }
                }, 500);
            }
        });

        listView = pullToLoadMoreListView.getListView();
        chatInputView = (ChatInputView) root.findViewById(R.id.inputView);
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle fragmentArgs = getArguments();
        chatType = fragmentArgs.getInt("chatType", MessageUtils.CHAT_SINGLE);
        toChatUsername = fragmentArgs.getString("userId");
        initView();
        myBroadcastReciver=new MyBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_FORWORD);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_EMPTY);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReciver, intentFilter);
    }

    private void initView() {
        chatInputView.initView(getActivity(), pullToLoadMoreListView);
        chatInputView.setInputViewLisenter(new MyInputViewLisenter());
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                chatInputView.hideSoftInput();
                chatInputView.interceptBackPress();

                return false;
            }
        });

        adapter = new ChatAdapter(presenter.getMessageList(), getActivity(), toChatUsername, chatType);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HTMessage htMessage = adapter.getItem(i);
                if (htMessage != null) {
                    showMsgDialog(htMessage, i);
                }

                return true;
            }
        });
        adapter.setOnResendViewClick(new ChatAdapter.OnResendViewClick() {
            @Override
            public void resendMessage(HTMessage htMessage) {
                showReSendDialog(htMessage);
            }
        });
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
            String myCopy = ACache.get(getActivity()).getAsString("myCopy");
            if (!TextUtils.isEmpty(myCopy)) {
                JSONObject jsonObject = JSONObject.parseObject(myCopy);
                String msgId = jsonObject.getString("msgId");
                String imagePath = jsonObject.getString("imagePath");
                HTMessage emMessage = presenter.getMessageById(msgId);
                if (emMessage == null) {
                    return true;
                }
                showCopyContent(jsonObject.getString("copyType"), jsonObject.getString("localPath"), emMessage, imagePath);
                return true;
            }
            return false;
        }

        @Override
        public void onEditTextUp() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPosition(listView.getCount() - 1);
                }
            }, 500);
        }


        @Override
        public void onAlbumItemClicked() {
            presenter.selectPicFromLocal();
        }

        @Override
        public void onPhotoItemClicked() {

            presenter.selectPicFromCamera();
        }

        @Override
        public void onLocationItemClicked() {
            presenter.selectLocation();

        }

        @Override
        public void onVideoItemClicked() {
            presenter.selectVideo();
        }

        @Override
        public void onCallItemClicked() {
            presenter.selectCall();

        }

        @Override
        public void onFileItemClicked() {

            presenter.selectFile();

        }
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
    public void showNoMoreMessage() {

    }

    @Override
    public void refreshListView() {
        adapter.notifyDataSetChanged();
        if (listView.getCount() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
    }

    @Override
    public Fragment getFragment() {
        return this;

    }


    /**
     * 复制
     *
     * @param copyType
     * @param localPath
     * @param message1
     * @param imagePath
     */
    public void showCopyContent(final String copyType, final String localPath, final HTMessage message1, final String imagePath) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.item_dialog_gridview, null);
        TextView tv_forward = (TextView) view.findViewById(R.id.tv_forward);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        tv_forward.setText(R.string.copy);
        textView.setText(R.string.really_copy_and_send);
        buidler.setView(view);
        if ("image".equals(copyType) && imagePath != null) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
        final AlertDialog dialog = buidler.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                presenter.sendCopyMessage(copyType, localPath, message1, imagePath);

            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showMsgDialog(final HTMessage message, final int postion) {

        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getActivity().getString(R.string.delete), getActivity().getString(R.string.copy), getActivity().getString(R.string.forward)});
        if (message.getDirect() == HTMessage.Direct.SEND) {
            HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getActivity().getString(R.string.delete), getActivity().getString(R.string.copy), getActivity().getString(R.string.forward), getActivity().getString(R.string.reback)});
        }
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) { //删除
                    presenter.deleteMessage(message);

                } else if (position == 1) { //复制
                    presenter.copyMessage(message);

                } else if (position == 2) {//转发
                    presenter.forwordMessage(message);

                } else if (position == 3) {//撤回
                    presenter.withdrowMessage(message, postion);
                }
            }
        });
    }

    /**
     * 重新发送消息
     *
     * @param htMessage
     */
    private void showReSendDialog(final HTMessage htMessage) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.item_diaolog_gridview, null);
        TextView tv_forward = (TextView) view.findViewById(R.id.tv_forward);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        tv_forward.setText(R.string.prompt);
        textView.setText(R.string.resend_text);
        buidler.setView(view);
        final AlertDialog dialog = buidler.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                presenter.resendMessage(htMessage);

            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
                String msgId = intent.getStringExtra("msgId");
                presenter.onMessageWithdrow(msgId);
            } else if (intent.getAction().equals(IMAction.ACTION_MESSAGE_FORWORD)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onMeesageForward(message);
            } else if (intent.getAction().equals(IMAction.ACTION_NEW_MESSAGE)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onNewMessage(message);
            } else if (IMAction.ACTION_MESSAGE_EMPTY.equals(intent.getAction())) {
                String id = intent.getStringExtra("id");
                if (toChatUsername.equals(id)){
                    presenter.onMessageClear();
                }
            }
        }
    }
}
