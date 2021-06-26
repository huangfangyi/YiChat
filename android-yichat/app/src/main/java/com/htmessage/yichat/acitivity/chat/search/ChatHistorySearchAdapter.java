package com.htmessage.yichat.acitivity.chat.search;

import android.app.Activity;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.update.data.UserManager;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.model.HTMessageTextBody;

import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：SearchChatHistoryAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/18 11:06
 * 邮箱:814326663@qq.com
 */
public class ChatHistorySearchAdapter extends BaseAdapter {
    private List<HTMessage> msgs;
    private Activity context;

    public ChatHistorySearchAdapter(List<HTMessage> msgs, Activity context) {
        this.msgs = msgs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public HTMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HTMessage message = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.search_history_message, null);
        }
        ChatViewHolder holder = (ChatViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ChatViewHolder();
            handleViewAndHolder(convertView, holder);
        }
        handleData(holder, message);
        return convertView;
    }


    private void handleViewAndHolder(View convertView, ChatViewHolder holder) {
        holder.reBubble = (RelativeLayout) convertView.findViewById(R.id.bubble);
        holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
        holder.tvNick = (TextView) convertView.findViewById(R.id.tv_userid);
        holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        holder.reMain = (RelativeLayout) convertView.findViewById(R.id.re_main);
        convertView.setTag(holder);
    }


    private void handleData(final ChatViewHolder holder, final HTMessage message) {
        HTMessageBody htMessageBody = message.getBody();
        if (htMessageBody == null) {
            return;
        }
        holder.tvNick.setVisibility(View.VISIBLE);
        String nick = getLastMessageNick(message);
        String avatar = getLastMessageAvatar(message);
        if (message.getDirect() == HTMessage.Direct.SEND) {
            if (message.getFrom().equals(HTApp.getInstance().getUsername())) {

            }
        }
        if (!TextUtils.isEmpty(nick)) {
            holder.tvNick.setText(nick);
        }
        UserManager.get().loadUserAvatar(context, avatar, holder.ivAvatar);
        if (message.getType() == HTMessage.Type.TEXT) {
            showTextView(message, holder);
        }
    }

    /**
     * 显示文字消息
     *
     * @param message
     * @param holder
     */
    private void showTextView(HTMessage message, ChatViewHolder holder) {
        HTMessageBody htMessageBody = message.getBody();
        holder.reMain.setVisibility(View.VISIBLE);
        Spannable smiledText = SmileUtils.getSmiledText(context, ((HTMessageTextBody) htMessageBody).getContent());
        holder.tvContent.setText(smiledText, TextView.BufferType.SPANNABLE);
    }

    /**
     * 获取最后消息的头像加载前面的头像
     *
     * @param htMessage
     * @return
     */
    private String getLastMessageAvatar(HTMessage htMessage) {
        String avatar = htMessage.getStringAttribute(HTConstant.JSON_KEY_AVATAR);
        String from = htMessage.getFrom();
        if (msgs != null) {
            if (msgs.size() > 0) {
                HTMessage message = msgs.get(msgs.size() - 1);
                String avatar1 = message.getStringAttribute(HTConstant.JSON_KEY_AVATAR);
                String userFrom = message.getFrom();
                if (from.equals(userFrom) && !TextUtils.isEmpty(avatar) && !TextUtils.isEmpty(avatar1) && !avatar1.equals(avatar)) {
                    avatar = avatar1;
                }
            }
        }
        return avatar;
    }

    /**
     * 获取最后消息的昵称加载前面的昵称
     *
     * @param htMessage
     * @return
     */
    private String getLastMessageNick(HTMessage htMessage) {
        String nick = htMessage.getStringAttribute(HTConstant.JSON_KEY_NICK);
        String from = htMessage.getFrom();
        if (msgs != null) {
            if (msgs.size() > 0) {
                HTMessage message = msgs.get(msgs.size() - 1);
                String fromNick = message.getStringAttribute(HTConstant.JSON_KEY_NICK);
                String userFrom = message.getFrom();
                if (from.equals(userFrom) && !TextUtils.isEmpty(nick) && !TextUtils.isEmpty(fromNick) && !fromNick.equals(nick)) {
                    nick = fromNick;
                }
            }
        }
        return nick;
    }

    public static class ChatViewHolder {
        public RelativeLayout reMain;
        public RelativeLayout reBubble;
        public ImageView ivAvatar;
        public TextView tvNick;
        //文本消息,位置消息,文件消息
        public TextView tvContent;
    }
}
