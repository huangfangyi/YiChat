package com.htmessage.fanxinht.acitivity.main.conversation;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.group.GroupNoticeMessageUtils;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.DateUtils;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;

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
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.tv_group_tag=(TextView) convertView.findViewById(R.id.tv_group_tag);
            holder.re_main=(RelativeLayout) convertView.findViewById(R.id.re_main);
            convertView.setTag(holder);
        }
        HTConversation htConversation = getItem(position);
        ChatType chatType = htConversation.getChatType();
        HTMessage htMessage = null;
        List<HTMessage> messages = htConversation.getAllMessages();
        if (messages.size() > 0) {
            htMessage = htConversation.getLastMessage();
        }
        String userId = htConversation.getUserId();
        if (chatType == ChatType.groupChat) {
            holder.tv_name.setMaxLines(1);
            holder.tv_name.setMaxEms(10);
            holder.tv_name.setEllipsize(TextUtils.TruncateAt.END);
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
            if (htGroup != null) {
                holder.tv_name.setText(htGroup.getGroupName());
                Glide.with(context).load(htGroup.getImgUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_group).into(holder.ivAvatar);

            }else {
                //如果当前群还未获取到,
                if(htMessage!=null&&htMessage.getIntAttribute("action",0)==2000){
                    String groupName=htMessage.getStringAttribute("groupName");
                       if(!TextUtils.isEmpty(groupName)){
                           holder.tv_name.setText(groupName);
                       }
                }
            }
            holder.tv_group_tag.setVisibility(View.VISIBLE);
        } else {
            User user = ContactsManager.getInstance().getContactList().get(userId);
            holder.tv_group_tag.setVisibility(View.INVISIBLE);
            if (user != null) {
                holder.tv_name.setText(user.getNick());
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(holder.ivAvatar);
            } else {
                holder.tv_name.setText(userId);
                Glide.with(context).load(R.drawable.default_avatar).into(holder.ivAvatar);
            }

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
        }else{
            holder.tv_content.setText("");
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(htConversation.getTime())));
        }
        if(htConversation.getTopTimestamp()!=0){

            holder.re_main.setBackgroundResource(R.drawable.list_item_bg_gray);
        }else{
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
        TextView tv_group_tag;
        RelativeLayout re_main;

    }

    protected final static String[] msgs = {"发来一条消息", "[图片消息]", "[语音消息]", "[位置消息]", "[视频消息]", "[文件消息]",
            "%1个联系人发来%2条消息"
    };

    private String getContent(HTMessage message) {
        int action=message.getIntAttribute("action",0);
        if(action>1999&&action<2005||action==3000){
           return GroupNoticeMessageUtils.getGroupNoticeContent(message);
        }

     //   HTConversation htConversation = ConversationManager.getInstance().getConversation(message.getFrom());
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
        return  notifyText;
    }

//    /**
//     * 根据消息内容和消息类型获取消息内容提示
//     *
//     * @param message
//     * @param context
//     * @return
//     */
//    private String getMessageDigest(HTMessage message, Context context) {
//        String digest = "";
//        switch (message.getType()) {
//        case LOCATION: // 位置消息
//            if (message.getDirect() == HTMessage.Direct.RECEIVE) {
//
//                digest = getStrng(context, R.string.location_recv);
//                digest = String.format(digest, message.getFrom());
//                return digest;
//            } else {
//                // digest = EasyUtils.getAppResourceString(context,
//                // "location_prefix");
//                digest = getStrng(context, R.string.location_prefix);
//            }
//            break;
//        case IMAGE: // 图片消息
//
//            digest = getStrng(context, R.string.picture);
//
//            break;
//        case VOICE:// 语音消息
//            digest = getStrng(context, R.string.voice);
//            break;
//        case VIDEO: // 视频消息
//            digest = getStrng(context, R.string.video);
//            break;
//        case TXT: // 文本消息
//            if (!message.getBooleanAttribute(
//                    Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                TextMessageBody txtBody = (TextMessageBody) message.getBody();
//                digest = txtBody.getMessage();
//            } else {
//                TextMessageBody txtBody = (TextMessageBody) message.getBody();
//                digest = getStrng(context, R.string.voice_call)
//                        + txtBody.getMessage();
//            }
//            break;
//        case FILE: // 普通文件消息
//            digest = getStrng(context, R.string.file);
//            break;
//        default:
//            System.err.println("error, unknow type");
//            return "";
//        }
//
//        return digest;
//    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }


}
