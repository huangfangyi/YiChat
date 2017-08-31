package com.htmessage.fanxinht.acitivity.chat;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.htmessage.sdk.model.HTMessage;
import com.htmessage.fanxinht.acitivity.BasePresenter;
import com.htmessage.fanxinht.acitivity.BaseView;

import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public interface ChatContract {

    interface View extends BaseView<Presenter> {
        void showNoMoreMessage();

        void refreshListView();

        Fragment getFragment();

    }

    interface Presenter extends BasePresenter {
        void loadMoreMessages();

        void onEditTextLongClick();

        void sendVideoMessage(String videoPath, String thumbPath, int duration);

        void sendTextMessage(String content);

        HTMessage getMessageById(String msgId);

        void selectPicFromCamera();

        void selectPicFromLocal();

        void selectLocation();

        void selectVideo();

        void selectFile();

        void selectCall();

        void onResult(int requestCode, int resultCode, Intent data);

        void sendVoiceMessage(String voiceFilePath, int voiceTimeLength);

        List<HTMessage> getMessageList();
        void resendMessage(HTMessage htMessage);
        void  deleteMessage(HTMessage htMessage);
        void  copyMessage(HTMessage htMessage);
        void  forwordMessage(HTMessage htMessage);
        void withdrowMessage(HTMessage htMessage,int position);

        void sendCopyMessage(String copyType,String localPath,HTMessage message, String imagePath);
        void onMessageWithdrow(String msgId);
        void onNewMessage(HTMessage htMessage);
        void onMeesageForward(HTMessage htMessage);
        void onMessageClear();
    }
}
