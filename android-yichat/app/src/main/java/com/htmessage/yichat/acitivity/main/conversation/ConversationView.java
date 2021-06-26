package com.htmessage.yichat.acitivity.main.conversation;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.yichat.acitivity.BaseView;

import java.util.List;

/**
 * Created by huangfangyi on 2017/6/27.
 * qq 84543217
 */

public interface ConversationView extends BaseView<ConversationPresenter> {

    void showItemDialog(HTConversation htConversation);
      void adapterRefresh();
      void showSmall(List<JSONObject> smallList);

}
