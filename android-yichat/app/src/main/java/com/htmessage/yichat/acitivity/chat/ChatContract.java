package com.htmessage.yichat.acitivity.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.acitivity.BaseView;

import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public interface ChatContract {

    interface View extends BaseView<Presenter> {
        void showToast(int resId);

        void insertRecyclerView(int position, int count,int type);

        void updateRecyclerView(int position );
        void loadMoreMessageRefresh(int position, int count);

        void initRecyclerView(List<HTMessage> messageList);
        void deleteItemRecyclerView(int position);
        //清空聊天记录刷新
        void notifyClear();

        void startToDialogRP(JSONObject jsonObject);
        void startToDetailRp(JSONObject jsonObject);
        void onGroupInfoLoaded();
        void showNewNoticeDialog(String title, String content, String id);
        void   setAtUserStytle(String realNick,boolean isChooseFromList);
    }

    interface Presenter extends BasePresenter {
        void   sendZhenMessage();
        void initData(Bundle bundle);

        void loadMoreMessages();

         void sendTextMessage(String content);
         void selectPicFromCamera(Activity activity);

        void selectPicFromLocal(Activity activity);
        void onResult(int requestCode, int resultCode, Intent data, Context context);

        void sendVoiceMessage(String voiceFilePath, int voiceTimeLength);

        void resendMessage(HTMessage htMessage);

        void deleteSingChatMessage(HTMessage htMessage);

        void withdrowMessage(HTMessage htMessage, int position);
        void onMessageWithdrow(HTMessage htMessage);

        void onNewMessage(HTMessage htMessage);

        void onMeesageForward(HTMessage htMessage);

        void onMessageClear();
        void onOpenRedpacket(HTMessage htMessage,String packetId);
        void sendRedCmdMessage(String whoisRed,String msgId);
        void getGroupInfoInServer(String groupId);

        void startCardSend(Activity activity);
        void setAtUser(String nick,String userId);
        boolean isHasAt(String userId);
        boolean isHasAtNick(String nick);
        void startChooseAtUser();
        void deleteAtUser(String nick);
        void refreshHistory();

    }
}
