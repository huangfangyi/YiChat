package com.htmessage.yichat.acitivity.main.conversation;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.group.GroupNoticeMessageUtils;
import com.htmessage.yichat.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.yichat.utils.LoggerUtils;

import java.util.Date;
import java.util.List;


public class ConversationAdapter extends BaseAdapter {
    private List<HTConversation> htConversations;
    private Context context;

    public ConversationAdapter(Context context, List<HTConversation> htConversations) {
        this.context = context;
        this.htConversations = htConversations;
    }

    @Override
    public int getCount() {
        return htConversations.size();
    }

    @Override
    public HTConversation getItem(int position) {

        return htConversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_conversation_single, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.message);
            holder.tv_time = (TextView) convertView.findViewById(R.id.time);
            holder.tv_unread = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.ivAvatar =  convertView.findViewById(R.id.avatar);
            holder.re_main = (ConstraintLayout) convertView.findViewById(R.id.re_main);
            holder.mentioned = (TextView) convertView.findViewById(R.id.mentioned);
            convertView.setTag(holder);
        }
        HTConversation htConversation = getItem(position);
        ChatType chatType = htConversation.getChatType();
        HTMessage htMessage = htConversation.getLastMessage();
//        List<HTMessage> messages = htConversation.getLastMessage();
//        if (messages != null && messages.size() > 0) {
//            htMessage = htConversation.getLastMessage();
//        }

//        if(htMessage==null){
//            htConversations.remove(position);
//            notifyDataSetChanged();
//            return convertView;
//        }
        String userId = htConversation.getUserId();
        if (chatType == ChatType.groupChat) {
            holder.tv_name.setMaxLines(1);
            holder.tv_name.setMaxEms(10);
            holder.tv_name.setEllipsize(TextUtils.TruncateAt.END);
            holder.ivAvatar.setImageResource(R.drawable.default_group);
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
            if (htGroup != null) {
                holder.tv_name.setText(htGroup.getGroupName());
                String imgUrl = htGroup.getImgUrl();
//                if (!TextUtils.isEmpty(imgUrl)) {
//                    if (!imgUrl.startsWith("http") || !imgUrl.contains(HTConstant.baseImgUrl)) {
//                        imgUrl = HTConstant.baseImgUrl + imgUrl;
//                    }
//                }
                 CommonUtils.loadGroupAvatar(context, imgUrl, holder.ivAvatar);
            } else {
                //如果当前群还未获取到,
                if (htMessage != null && htMessage.getIntAttribute("action", 0) == 2000) {
                    String groupName = htMessage.getStringAttribute("groupName");
                    if (!TextUtils.isEmpty(groupName)) {
                        holder.tv_name.setText(groupName);

                    }else{
                        holder.tv_name.setText(userId);
                    }

                }else {
                    holder.tv_name.setText(userId);
                }
                CommonUtils.loadGroupAvatar(context, null, holder.ivAvatar);
            }

            if(!GroupInfoManager.getInstance().getAtTag(userId)){
                holder.mentioned.setVisibility(View.GONE);
            }else {
                holder.mentioned.setVisibility(View.VISIBLE);
            }

          //  holder.tv_group_tag.setVisibility(View.INVISIBLE);
        } else {
            holder.mentioned.setVisibility(View.GONE);
            holder.ivAvatar.setImageResource(R.drawable.default_avatar);
            String nick = UserManager.get().getUserNick(userId);
            String userAvatar =UserManager.get().getUserAvatar(userId);
            holder.tv_name.setText(nick);
             UserManager.get().loadUserAvatar(context, userAvatar, holder.ivAvatar);

        }
        if (htConversation.getUnReadCount() > 0) {
            // show unread message count
            holder.tv_unread.setText(String.valueOf(htConversation.getUnReadCount()));
            holder.tv_unread.setVisibility(View.VISIBLE);
        } else {
            holder.tv_unread.setVisibility(View.INVISIBLE);
        }
        if (htMessage != null) {
            holder.tv_content.setText(SmileUtils.getSmiledText(context, getContent(htMessage)),
                    TextView.BufferType.SPANNABLE);
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(htMessage.getTime())));
          // LoggerUtils.e("time----->"+htMessage.getTime()+"-----"+DateUtils.getTimestampString(new Date(htMessage.getTime())));
        } else {
            holder.tv_content.setText("");
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(htConversation.getTime())));
        }
        if (htConversation.getTopTimestamp() != 0) {

            holder.re_main.setBackgroundResource(R.drawable.list_item_bg_gray);
        } else {
            holder.re_main.setBackgroundResource(R.drawable.list_item_bg_white);
        }

        return convertView;
    }


    private static class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView tv_name;
        /**
         * 消息未读数
         */
        TextView tv_unread;
        /**
         * 最后一条消息的内容
         */
        TextView tv_content;
        /**
         * 最后一条消息的时间
         */
        TextView tv_time;

        ImageView ivAvatar;

        //群组的标识
        ConstraintLayout re_main;
        //有人at我
        TextView mentioned;
    }

    protected final static String[] msgs = {"发来一条消息", "[图片消息]", "[语音消息]", "[位置消息]", "[视频消息]", "[文件消息]",
            "%1个联系人发来%2条消息"
    };

    private String getContent(HTMessage message) {
        int action = message.getIntAttribute("action", 0);
        if (action > 1999 && action < 2005 || action == 3000||action==6001) {
            return GroupNoticeMessageUtils.getGroupNoticeContent(message);
        }
        String notifyText = "";
        if (message.getType() == null) {
            return "";
        }
        switch (message.getType()) {
            case TEXT:
                HTMessageTextBody textBody = (HTMessageTextBody) message.getBody();
                String content = textBody.getContent();
                if (content != null) {
                    notifyText += content;
                } else {
                    notifyText += msgs[0];
                }
                break;
            case IMAGE:
                notifyText += msgs[1];
                break;
            case VOICE:
                notifyText += msgs[2];
                break;
            case LOCATION:
                notifyText += msgs[3];
                break;
            case VIDEO:
                notifyText += msgs[4];
                break;
            case FILE:
                notifyText += msgs[5];
                break;
        }
        return notifyText;
    }
}
