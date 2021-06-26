package com.htmessage.yichat.acitivity.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.sdk.utils.Logger;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTClientHelper;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.voice.VoicePlayClickListener;
import com.htmessage.yichat.acitivity.chat.weight.emojicon.EmojiconDatas;
import com.htmessage.yichat.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.yichat.acitivity.chat.weight.videoview.Utils;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.yichat.utils.HTPathUtils;
import com.htmessage.yichat.utils.ImageUtils;
import com.htmessage.yichat.utils.LinkifySpannableUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.OpenFileUtils;
import com.joooonho.SelectableRoundedImageView;

import java.io.File;
import java.util.Date;
import java.util.List;


/**
 * Created by huangfangyi on 2016/11/24.
 * qq 84543217
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<HTMessage> msgs;
    private Activity context;
    private static final int MESSAGE_TEXT_RECEIVED = 0;
    private static final int MESSAGE_TEXT_SEND = 1;
    private static final int MESSAGE_IMAGE_RECEIVED = 2;
    private static final int MESSAGE_IMAGE_SEND = 3;
    private static final int MESSAGE_VOICE_RECEIVED = 4;
    private static final int MESSAGE_VOICE_SEND = 5;
    private static final int MESSAGE_FILE_RECEIVED = 6;
    private static final int MESSAGE_FILE_SEND = 7;
    private static final int MESSAGE_VEDIO_RECEIVED = 8;
    private static final int MESSAGE_VEDIO_SEND = 9;
    private static final int MESSAGE_LOCATION_RECEIVED = 10;
    private static final int MESSAGE_LOCATION_SEND = 11;

    private static final int MESSAGE_RED_RECEIVED = 12;
    private static final int MESSAGE_RED_SEND = 13;
    private static final int MESSAGE_TRANSFER_RECEIVED = 14;
    private static final int MESSAGE_TRANSFER_SEND = 15;

    private static final int MESSAGE_NOTICE = 16;
    private static final int MESSAGE_CARD_RECEIVED = 17;
    private static final int MESSAGE_CARD_SEND = 18;
    private static final int MESSAGE_BIGEMOJI_RECEIVED = 19;
    private static final int MESSAGE_BIGEMOJI_SEND = 20;
    private String chatTo;
    private int chatType;

    public ChatAdapter(final List<HTMessage> msgs, Activity context, String chatTo, int chatType) {
        this.msgs = msgs;
        this.context = context;
        this.chatTo = chatTo;
        this.chatType = chatType;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public HTMessage getItem(int position) {
        return msgs.get(position);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = getViewByType(viewType);
        MyViewHolder myViewHolder = new MyViewHolder(convertView, viewType);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        handleData(holder, position);
    }


    @Override
    public int getItemViewType(int position) {
        HTMessage message = msgs.get(position);
        return getItemViewType(message);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;
        public TextView tvNick;
        public TextView timeStamp;
        public ImageView ivMsgStatus;
        //文本消息,位置消息,文件消息
        public TextView tvContent;
        //图片消息,视频消息,位置消息,文件消息
        public SelectableRoundedImageView ivContent;
        //语音消息
        public TextView tvDuration;
        public ImageView ivUnread;
        public ImageView ivVoice;

        //红包消息
        public TextView tv_red_name;
        public TextView tv_red_content;
        public ConstraintLayout conRedpacket;

        //发送消息
        public ProgressBar progressBar;
        //个人名片
        public TextView tv_card_name;
        public ImageView iv_card_avatar;
        //文件消息
        public TextView tvFileSize;

        //通知消息只有一个textView
        public TextView tv_notice;
        //动态表情
        public  ImageView  iv_big_emoji;


        public MyViewHolder(View convertView, int viewType) {
            super(convertView);
            handleViewAndHolder(viewType, convertView, this);
        }
    }


    private int getItemViewType(HTMessage htMessage) {
        HTMessage.Type type = htMessage.getType();
        int action = htMessage.getIntAttribute("action", 0);
        if (action == 10001) {
            //红包消息
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_RED_RECEIVED : MESSAGE_RED_SEND;
        } else if (action == 10002) {
            //转账消息
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_TRANSFER_RECEIVED : MESSAGE_TRANSFER_SEND;
        } else if ((action < 2006 && action > 1999) || action == 6001 || action == 10004 || action == 10005) {
            //群相关通知消息||撤回消息||红包领取通知消息||转账领取通知消息
            return MESSAGE_NOTICE;
        } else if (action == 10007) {
            //名片消息
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_CARD_RECEIVED : MESSAGE_CARD_SEND;
        } else if (type == HTMessage.Type.TEXT) {
            String content=((HTMessageTextBody)htMessage.getBody()).getContent();

            if(EmojiconDatas.getGemojeMap().containsKey(content)) {
                return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_BIGEMOJI_RECEIVED : MESSAGE_BIGEMOJI_SEND;
            }

            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_TEXT_RECEIVED : MESSAGE_TEXT_SEND;
        } else if (type == HTMessage.Type.IMAGE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_IMAGE_RECEIVED : MESSAGE_IMAGE_SEND;
        } else if (type == HTMessage.Type.VOICE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VOICE_RECEIVED : MESSAGE_VOICE_SEND;
        } else if (type == HTMessage.Type.VIDEO) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VEDIO_RECEIVED : MESSAGE_VEDIO_SEND;
        } else if (type == HTMessage.Type.FILE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_FILE_RECEIVED : MESSAGE_FILE_SEND;
        } else if (type == HTMessage.Type.LOCATION) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_LOCATION_RECEIVED : MESSAGE_LOCATION_SEND;
        }
        return 0;
    }


    private View getViewByType(int viewType) {

        switch (viewType) {
            case MESSAGE_TEXT_RECEIVED:
                return View.inflate(context, R.layout.row_received_message, null);
            case MESSAGE_IMAGE_RECEIVED:
                return View.inflate(context, R.layout.row_received_picture, null);
            case MESSAGE_VOICE_RECEIVED:
                return View.inflate(context, R.layout.row_received_voice, null);
            case MESSAGE_FILE_RECEIVED:
                return View.inflate(context, R.layout.row_received_file, null);
            case MESSAGE_VEDIO_RECEIVED:
                return View.inflate(context, R.layout.row_received_video, null);
            case MESSAGE_LOCATION_RECEIVED:
                return View.inflate(context, R.layout.row_received_location, null);
            case MESSAGE_TEXT_SEND:
                return View.inflate(context, R.layout.row_sent_message, null);
            case MESSAGE_IMAGE_SEND:
                return View.inflate(context, R.layout.row_sent_picture, null);
            case MESSAGE_VOICE_SEND:
                return View.inflate(context, R.layout.row_sent_voice, null);
            case MESSAGE_FILE_SEND:
                return View.inflate(context, R.layout.row_sent_file, null);
            case MESSAGE_VEDIO_SEND:
                return View.inflate(context, R.layout.row_sent_video, null);
            case MESSAGE_LOCATION_SEND:
                return View.inflate(context, R.layout.row_sent_location, null);
            case MESSAGE_RED_SEND: //红包
                return View.inflate(context, R.layout.row_send_red, null);
            case MESSAGE_RED_RECEIVED:
                return View.inflate(context, R.layout.row_receive_red, null);
            case MESSAGE_TRANSFER_SEND: //转账
                return View.inflate(context, R.layout.row_send_transfer, null);
            case MESSAGE_TRANSFER_RECEIVED:
                return View.inflate(context, R.layout.row_receive_transfer, null);
            case MESSAGE_NOTICE:
                return View.inflate(context, R.layout.row_notice, null);
            case MESSAGE_CARD_RECEIVED:
                return View.inflate(context, R.layout.row_card_received, null);
            case MESSAGE_CARD_SEND:
                return View.inflate(context, R.layout.row_card_send, null);
            case MESSAGE_BIGEMOJI_RECEIVED:
                return   View.inflate(context, R.layout.row_received_bigemoji, null);
            case MESSAGE_BIGEMOJI_SEND:
                return   View.inflate(context, R.layout.row_sent_bigemoji, null);

            default:
                return View.inflate(context, R.layout.row_sent_message, null);
        }
    }


    private void handleViewAndHolder(int viewType, View convertView, MyViewHolder holder) {
        if (viewType == MESSAGE_NOTICE) {
            holder.tv_notice = convertView.findViewById(R.id.tv_notice);
            convertView.setTag(holder);
            //return很重要
            return;
        }
        holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
        holder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
        holder.tvNick = (TextView) convertView.findViewById(R.id.tv_userid);

        if (viewType == MESSAGE_TEXT_RECEIVED || viewType == MESSAGE_IMAGE_RECEIVED || viewType == MESSAGE_VOICE_RECEIVED
                || viewType == MESSAGE_VEDIO_RECEIVED || viewType == MESSAGE_FILE_RECEIVED
                || viewType == MESSAGE_LOCATION_RECEIVED
                || viewType == MESSAGE_RED_RECEIVED
                || viewType == MESSAGE_TRANSFER_RECEIVED) {
            //接收消息,在群聊时可以显示群成员名称
        } else {
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            holder.ivMsgStatus = (ImageView) convertView.findViewById(R.id.msg_status);
        }

        if (viewType == MESSAGE_TEXT_RECEIVED || viewType == MESSAGE_TEXT_SEND
                || viewType == MESSAGE_LOCATION_SEND || viewType == MESSAGE_LOCATION_RECEIVED
                || viewType == MESSAGE_FILE_RECEIVED || viewType == MESSAGE_FILE_SEND) {
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        }
        if (viewType == MESSAGE_IMAGE_SEND || viewType == MESSAGE_IMAGE_RECEIVED) {
            holder.ivContent = convertView.findViewById(R.id.image);
        }
        if (viewType == MESSAGE_VOICE_SEND || viewType == MESSAGE_VOICE_RECEIVED) {
            holder.tvDuration = (TextView) convertView.findViewById(R.id.tv_length);
            holder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_voice);
            if (viewType == MESSAGE_VOICE_RECEIVED) {
                holder.ivUnread = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
            }
        }

        if (viewType == MESSAGE_VEDIO_RECEIVED || viewType == MESSAGE_VEDIO_SEND) {
            holder.ivContent = convertView.findViewById(R.id.image);
            holder.tvDuration = convertView.findViewById(R.id.chatting_length_iv);
        }
//        if (viewType == MESSAGE_LOCATION_RECEIVED || viewType == MESSAGE_LOCATION_SEND) {
//            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//            holder.ivContent = (ImageView) convertView.findViewById(R.id.image);
//        }
        if (viewType == MESSAGE_RED_SEND || viewType == MESSAGE_RED_RECEIVED) {
            holder.tv_red_name = (TextView) convertView.findViewById(R.id.tv_red_name);
            holder.tv_red_content = (TextView) convertView.findViewById(R.id.tv_red_content);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.conRedpacket = convertView.findViewById(R.id.con_redpacket);

        }
        if (viewType == MESSAGE_TRANSFER_SEND || viewType == MESSAGE_TRANSFER_RECEIVED) {
            holder.tv_red_name = (TextView) convertView.findViewById(R.id.tv_red_name);
            holder.tv_red_content = (TextView) convertView.findViewById(R.id.tv_red_content);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
        }

        if (viewType == MESSAGE_FILE_SEND || viewType == MESSAGE_FILE_RECEIVED) {
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        }

        if (viewType == MESSAGE_CARD_RECEIVED || viewType == MESSAGE_CARD_SEND) {
            holder.iv_card_avatar = (ImageView) convertView.findViewById(R.id.iv_card_avatar);
            holder.tv_card_name = (TextView) convertView.findViewById(R.id.tv_card_name);
            holder.conRedpacket= (ConstraintLayout) convertView.findViewById(R.id.con_redpacket);
        }

        if(viewType==MESSAGE_BIGEMOJI_RECEIVED||viewType==MESSAGE_BIGEMOJI_SEND){
            holder.iv_big_emoji = convertView.findViewById(R.id.image);
        }

        convertView.setTag(holder);

    }


    private void handleData(MyViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        final HTMessage message = getItem(position);
        if (viewType == MESSAGE_NOTICE) {
            holder.tv_notice.setText(getGroupNoticeContent(message));
            return;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onItemClick(message, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onItemLongClick(message, position);
                }
                return true;
            }
        });

        if (holder.tvContent != null) {
            holder.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onResendViewClick != null) {
                        onResendViewClick.onItemLongClick(message, position);
                    }
                    return true;
                }
            });
        }

        if (message.getDirect() == HTMessage.Direct.SEND && message.getStatus() == HTMessage.Status.FAIL) {
            holder.ivMsgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onResendViewClick != null) {
                        onResendViewClick.resendMessage(message);
                    }
                }
            });
        }
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getItem(position).getUsername();
                if (message.getChatType() == ChatType.groupChat) {
                    userId = message.getFrom();
                }

                if (onResendViewClick != null) {
                    onResendViewClick.onAvatarClick(userId);
                }
            }
        });
        holder.ivAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String userId = getItem(position).getUsername();
                if (message.getChatType() == ChatType.groupChat) {
                    userId = message.getFrom();
                }
                if (onResendViewClick != null) {
                    onResendViewClick.onAvatarLongClick(userId);
                }
                return true;
            }
        });
        HTMessageBody htMessageBody = message.getBody();
        if (htMessageBody == null) {
            return;
        }
        if (position == 0) {
            holder.timeStamp.setText(DateUtils.getTimestampString(new Date(message.getTime())));
            holder.timeStamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息大于1分钟
            long duration = message.getTime() - getItem(position - 1).getTime();
            if (duration >= 60000) {
                holder.timeStamp.setText(DateUtils.getTimestampString(new Date(message.getTime())));
                holder.timeStamp.setVisibility(View.VISIBLE);
            } else if (duration >= 0 && duration < 60000) {
                holder.timeStamp.setVisibility(View.GONE);
            } else if (duration < 0) {
                holder.timeStamp.setVisibility(View.GONE);
                //信息排序出现出现错误
                //  refreshData();
            } else {
                holder.timeStamp.setVisibility(View.GONE);
            }
        }

        if (chatType == MessageUtils.CHAT_GROUP && message.getDirect() == HTMessage.Direct.RECEIVE) {
            holder.tvNick.setVisibility(View.VISIBLE);

        } else if (chatType == MessageUtils.CHAT_SINGLE && message.getDirect() == HTMessage.Direct.RECEIVE) {
            holder.tvNick.setVisibility(View.GONE);
        }

        if (message.getDirect() == HTMessage.Direct.SEND) {
            UserManager.get().loadUserAvatar(context, UserManager.get().getMyAvatar(), holder.ivAvatar);
        } else {

            //先从缓存取，缓存没有再从消息体中取
            String userId = message.getUsername();
            if (message.getChatType() == ChatType.groupChat) {
                userId = message.getFrom();
            }
            String nick = UserManager.get().getUserNick(userId);
            String avatar = UserManager.get().getUserAvatar(userId);
            if (TextUtils.isEmpty(nick)) {
                //此处只需要判断nick是否为空，因为avatar即使本地存了，也是为空的
                nick = message.getStringAttribute("nick");
                avatar = message.getStringAttribute("avatar");
            }
            //先判断是否是好友如果是从好友列表取
            holder.tvNick.setText(TextUtils.isEmpty(nick) ? message.getFrom() : nick);
            if (chatType == MessageUtils.CHAT_GROUP) {
                Drawable drawable=null;
                HTGroup htGroup=HTClient.getInstance().groupManager().getGroup(chatTo);
                if(htGroup!=null){
                    if(htGroup.getOwner().equals(message.getFrom())){
                        drawable= ContextCompat.getDrawable(context,R.drawable.lay_icon_group_owner);

                    }else if(GroupInfoManager.getInstance().userIsManager( message.getFrom(),htGroup.getGroupId())){
                        drawable= ContextCompat.getDrawable(context,R.drawable.lay_icon_group_manager);
                    }else {
                        drawable=null;
                    }
                }
                holder.tvNick.setCompoundDrawablesWithIntrinsicBounds(null,null, drawable,null);
                holder.tvNick.setCompoundDrawablePadding(10);
            }
            UserManager.get().loadUserAvatar(context, TextUtils.isEmpty(avatar) ? "false" : avatar, holder.ivAvatar);

        }
        HTMessage.Status status = message.getStatus();
        if (message.getDirect() == HTMessage.Direct.SEND) {
            if (status == HTMessage.Status.CREATE && holder.progressBar != null) {
                //通知消息没有progressBar
                holder.progressBar.setVisibility(View.VISIBLE);
            } else {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
            if (status == HTMessage.Status.FAIL && holder.ivMsgStatus != null) {
                //通知消息没有ivMsgStatus
                holder.ivMsgStatus.setVisibility(View.VISIBLE);
            } else {
                if (holder.ivMsgStatus != null) {
                    holder.ivMsgStatus.setVisibility(View.GONE);

                }
            }
        }
        if (message.getType() == HTMessage.Type.TEXT) {

            showTextView(message, htMessageBody, holder,position);

        } else if (message.getType() == HTMessage.Type.IMAGE) {
            showImageView(message, holder, position);
        } else if (message.getType() == HTMessage.Type.VOICE) {
            showVoiceView(message, holder, position);
        } else if (message.getType() == HTMessage.Type.VIDEO) {
            showVideoView(message, holder, position);
        }
    }


    private void showTextView(HTMessage message, HTMessageBody htMessageBody, MyViewHolder holder, int position) {
        String contentTemp=((HTMessageTextBody)htMessageBody).getContent();
        if( EmojiconDatas.getGemojeMap().containsKey(contentTemp )){
            showBigEmoji(contentTemp,holder);
            return;
        }

        int action = message.getIntAttribute("action", 0);
        if (action == 10001) {
            //红包消息
            showRedView(message, holder,position);
        } else if (action == 10002) {
            //转账消息
            showTransferView(message, holder);
        } else if (action == 10007) {

            showCardView(message, holder,position);
            //名片消息
        } else {
            String content = ((HTMessageTextBody) htMessageBody).getContent();
            if (!TextUtils.isEmpty(content)) {
                if (holder.tvContent != null) {


                    holder.tvContent.setText(SmileUtils.getSmiledText(context, content), TextView.BufferType.SPANNABLE);
                    // holder.tvContent.setMovementMethod(LinkMovementClickMethod.getInstance());
                    LinkifySpannableUtils.getInstance().setSpan(context, holder.tvContent, 99999);




                }
            }
        }
    }

    private void   showBigEmoji(String contentTemp, MyViewHolder holder){

        Glide.with(context).load(EmojiconDatas.getGemojeMap().get(contentTemp)).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into( holder.iv_big_emoji); //加载一次



    }
    /**
     * 显示个人的名片
     *
     * @param message
     * @param holder
     */
    private void showCardView(HTMessage message, MyViewHolder holder,int position) {
        JSONObject jsonObject = message.getAttributes();
        Log.d("showCardView",jsonObject.toJSONString());
        final String cardUserId = jsonObject.getString("cardUserId");
        String cardUserNick = jsonObject.getString("cardUserNick");
        String cardUserAvatar = jsonObject.getString("cardUserAvatar");
        holder.tv_card_name.setText(TextUtils.isEmpty(cardUserNick) ? cardUserId : cardUserNick);
        UserManager.get().loadUserAvatar(context, cardUserAvatar, holder.iv_card_avatar);
        holder.conRedpacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(HTConstant.JSON_KEY_USERID, cardUserId);
//                if (chatType == MessageUtils.CHAT_GROUP) {
//                    intent.putExtra("groupId", chatTo);
//                }
                context.startActivity(intent);
            }
        });
        holder.conRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.  onItemLongClick(message, position);
                }


                return true;
            }
        });
    }

    /**
     * 转账
     *
     * @param message
     * @param holder
     */
    private void showTransferView(final HTMessage message, MyViewHolder holder) {
        final JSONObject jsonObject = message.getAttributes();
        final String transferId = jsonObject.getString("transferId");
        String amountStr = jsonObject.getString("amountStr");
        String msg = jsonObject.getString("msg");
        final String userId = jsonObject.getString("userId");

        final String avatar = jsonObject.getString("avatar");
        if (message.getDirect() == HTMessage.Direct.SEND) {
            if (TextUtils.isEmpty(msg)) {
                //User user=ContactsManager.get().getContactList().get(message.getTo()) ;


                holder.tv_red_content.setText(String.format(context.getString(R.string.transfer_money_to), UserManager.get().getUserNick(message.getTo())));

            } else {
                holder.tv_red_content.setText(msg);
            }
        } else {
            if (TextUtils.isEmpty(msg)) {
                holder.tv_red_content.setText(String.format(context.getString(R.string.transfer_money_to), context.getString(R.string.you)));
            } else {
                holder.tv_red_content.setText(msg);
            }
        }
        holder.tvContent.setText(context.getString(R.string.transfer_content));
        holder.tv_red_name.setText("￥" + amountStr);
//        holder.reBubble.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (message.getChatType() == ChatType.groupChat) {
////                    return;
////                } else {
////                    if (onResendViewClick != null) {
////                        onResendViewClick.onTransferMessageClicked(message, transferId);
////                    }
////                }
//            }
//        });
    }

    /**
     * 红包
     *
     * @param htMessage
     * @param holder
     */
    private void showRedView(final HTMessage htMessage, MyViewHolder holder,int position) {
        final JSONObject jsonObject = htMessage.getAttributes();
        String envMsg = jsonObject.getString("envMsg");
        String envName = jsonObject.getString("envName");
        final String envId = jsonObject.getString("envId");
        holder.tvContent.setText(envName);
        holder.tv_red_name.setText(envMsg);
        holder.tv_red_content.setText(context.getString(R.string.get_red));
        holder.conRedpacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onRedMessageClicked(htMessage, envId);
                }
            }
        });
        holder.conRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.  onItemLongClick(htMessage, position);
                }

                return true;
            }
        });


    }


    private void showFileView(final HTMessage htMessage, MyViewHolder holder) {
        final HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
        String size = getPrintSize(htMessageFileBody.getSize());
        final String fileName = htMessageFileBody.getFileName();
        holder.tvContent.setText(fileName);
        holder.tvFileSize.setText(size);
        final String localPath = htMessageFileBody.getLocalPath();
        final String remotePath = htMessageFileBody.getRemotePath();
//        holder.reBubble.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(localPath)) {
//                    File localFile = new File(localPath);
//                    if (localFile.exists()) {
//                        OpenFileUtils.openFile(localFile, context);
//                    } else {
//                        downLoadFile(htMessageFileBody, htMessage, remotePath, fileName);
//                    }
//                } else {
//                    downLoadFile(htMessageFileBody, htMessage, remotePath, fileName);
//                }
//            }
//        });
    }

    /**
     * 下载文件并打开
     *
     * @param htMessageFileBody 文件体
     * @param htMessage         消息
     * @param remotePath        远程地址
     * @param fileName          文件名字
     */
    private void downLoadFile(final HTMessageFileBody htMessageFileBody, final HTMessage htMessage, String remotePath, String fileName) {
        CommonUtils.showDialogNumal(context, context.getString(R.string.downwaiting));
        HTPathUtils htPathUtils = new HTPathUtils(chatTo, context);
        final String filePath = htPathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
        Log.d("filePath11--->", filePath);
        new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                CommonUtils.cencelDialog();
                File file = new File(filePath);
                if (file.exists()) {
                    htMessageFileBody.setLocalPath(filePath);
                    htMessage.setBody(htMessageFileBody);
                    HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                    OpenFileUtils.openFile(file, context);
                }
            }

            @Override
            public void onFailure(String message) {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(context, context.getString(R.string.Failed_to_download_file) + message);
            }
        });
    }


    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + " B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + " KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + " MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + " GB";
        }
    }

    private void showImageView(final HTMessage htMessage, final MyViewHolder holder, int position) {
        if (holder.ivContent == null) {
            return;
        }
        Logger.d("showImageView--->",htMessage.toXmppMessageBody());
        HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();

        //从本地取bitmap
        Bitmap bitmap = ChatFileManager.get().getMsgImageBitmap(htMessage.getMsgId());
        if (bitmap != null) {
            //本地缓存
            holder.ivContent.setImageBitmap(bitmap);
        } else {
            String localPath = htMessageImageBody.getLocalPath();

            if (localPath != null && new File(localPath).exists()) {
                File file= new File(localPath);
                //  Bitmap bitmap1= ImageUtils.getThumbnailImage(localPath,300,300);


//                Glide.with(context).load(localPath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource != null) {
//                            holder.ivContent.setImageBitmap(resource);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ChatFileManager.get().setMsgImageBitmap(htMessage.getMsgId(), resource);
//
//                                }
//                            }).start();
//                        }
//
//                    }
//                });
                if(file.length()> 1024 * 1024){
                    holder.ivContent.setImageURI(Uri.parse(ImageUtils.getThumbnailImage(localPath)));
                }else {

                    holder.ivContent.setImageURI(Uri.parse(localPath));
                }


            } else {
                String remotePath = htMessageImageBody.getRemotePath();
                //网络请求
                holder.ivContent.setImageResource(R.drawable.default_chat_image);
                if(remotePath!=null){
                    if (remotePath.contains(HTClientHelper.baseOssUrl)) {
                        remotePath = remotePath + HTConstant.chat_baseImgUrl_set;
                    }
                    Glide.with(context).load(remotePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (resource != null) {
                                holder.ivContent.setImageBitmap(resource);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ChatFileManager.get().setMsgImageBitmap(htMessage.getMsgId(), resource);

                                    }
                                }).start();
                            }

                        }
                    });
                }

            }


        }

        holder.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onResendViewClick != null) {

                    onResendViewClick.onImageMessageClick(htMessage);

                }
            }
        });

        holder.ivContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onItemLongClick(htMessage, position);
                }
                return true;

            }
        });


    }


    private void showVoiceView(final HTMessage htMessage, final MyViewHolder holder, int position) {
        HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();

        if (htMessageVoiceBody == null || holder.ivVoice == null || holder.tvDuration == null) {
            return;
        }
        int len = htMessageVoiceBody.getAudioDuration();
        if (len > 0) {
            holder.tvDuration.setText(len + "\"");
            holder.tvDuration.setVisibility(View.VISIBLE);
        } else {
            holder.tvDuration.setVisibility(View.INVISIBLE);
        }
        if (VoicePlayClickListener.playMsgId != null
                && VoicePlayClickListener.playMsgId.equals(htMessage.getMsgId()) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
                holder.ivVoice.setImageResource(+R.anim.voice_from_icon);
            } else {
                holder.ivVoice.setImageResource(+R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.ivVoice.getDrawable();
            voiceAnimation.start();
        } else {
            if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
                holder.ivVoice.setImageResource(R.drawable.ad1);
            } else {
                holder.ivVoice.setImageResource(R.drawable.adj);
            }
        }

//        if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
//            if (htMessage.getStatus() == HTMessage.Status.SUCCESS || htMessage.getStatus() == HTMessage.Status.ACKED) {
//                holder.ivUnread.setVisibility(View.INVISIBLE);
//            } else {
//                holder.ivUnread.setVisibility(View.VISIBLE);
//            }
//        }
        if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
            if (ChatFileManager.get().getLocalPath(htMessage.getMsgId(), htMessage.getType()) != null) {
                //如果本地有文件表示已经听过
                holder.ivUnread.setVisibility(View.INVISIBLE);
            } else {
                holder.ivUnread.setVisibility(View.VISIBLE);

            }
        }
        holder.ivVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VoicePlayClickListener(htMessage, chatTo, holder.ivVoice, holder.ivUnread, ChatAdapter.this, (context)).onClick(holder.ivVoice);
            }
        });
        //防止正在播放语音时，突然关闭了聊天页导致的闪退
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
            holder.ivVoice.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (VoicePlayClickListener.currentPlayListener != null && VoicePlayClickListener.isPlaying) {
                        VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    }
                }
            });
        }

        holder.ivVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onItemLongClick(htMessage, position);
                }
                return true;

            }
        });
    }


    private void showVideoView(final HTMessage message, final MyViewHolder holder, int position) {
        Log.d("showVideoView--->", message.toXmppMessageBody());
        final HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) message.getBody();
        int duration = htMessageVideoBody.getVideoDuration();
        if (duration > 0) {
            String time = DateUtils.secToTime(duration);
            holder.tvDuration.setText(time);
        }
        Bitmap bitmap = ChatFileManager.get().getMsgImageBitmap(message.getMsgId());
        if (bitmap != null) {
            Log.d("duration--->", duration + "");

            holder.ivContent.setImageBitmap(bitmap);
        } else {
            String localPathThumbnail = htMessageVideoBody.getLocalPathThumbnail();
            if (localPathThumbnail != null) {
////                holder.ivContent.setImageURI(Uri.parse(localPathThumbnail));
//                Glide.with(context).load(localPathThumbnail).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource != null) {
//                            holder.ivContent.setImageBitmap(resource);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ChatFileManager.get().setMsgImageBitmap(message.getMsgId(), resource);
//
//                                }
//                            }).start();
//                        }
//
//                    }
//                });
                File file=new File(localPathThumbnail);
                if(file.length()> 1024 * 1024){
                    holder.ivContent.setImageURI(Uri.parse(ImageUtils.getThumbnailImage(localPathThumbnail)));
                }else {
                    holder.ivContent.setImageURI(Uri.parse(localPathThumbnail));
                }

            } else {
                holder.ivContent.setImageResource(R.drawable.default_chat_image);

                String remoteThumbPath = htMessageVideoBody.getRemotePath() + HTConstant.chat_baseVideoUrl_set;
                Glide.with(context).load(remoteThumbPath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        holder.ivContent.setImageBitmap(resource);
                        ChatFileManager.get().setMsgImageBitmap(message.getMsgId(), resource);
                    }
                });
            }


        }


        holder.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onVideoMessageClick(message);
                }
            }
        });

        holder.ivContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onResendViewClick != null) {
                    onResendViewClick.onItemLongClick(message, position);
                }
                return true;

            }
        });


    }


//    private void showLocationView(final HTMessage message, MyViewHolder holder) {
//        final HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) message.getBody();
//        holder.tvContent.setText(htMessageLocationBody.getAddress());
//        showImageView(message, holder, false, true);
////        holder.reBubble.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(context, GdMapNavigationActivity.class);
////                intent.putExtra("latitude", htMessageLocationBody.getLatitude());
////                intent.putExtra("longitude", htMessageLocationBody.getLongitude());
////                intent.putExtra("address", htMessageLocationBody.getAddress());
////                context.startActivity(intent);
////            }
////        });
//    }

    private OnResendViewClick onResendViewClick;

    public void setOnResendViewClick(OnResendViewClick onResendViewClick) {
        this.onResendViewClick = onResendViewClick;
    }

    interface OnResendViewClick {
        void resendMessage(HTMessage htMessage);

        void onRedMessageClicked(HTMessage htMessage, String evnId);

        void onTransferMessageClicked(JSONObject jsonObject, String evnId);

        void onAvatarLongClick(String userId);

        void onItemLongClick(HTMessage htMessage, int position);

        void onItemClick(HTMessage htMessage, int position);

        void onAvatarClick(String userId);

        void onImageMessageClick(HTMessage htMessage);

        void onVideoMessageClick(HTMessage htMessage);

    }


    private static String getGroupNoticeContent(HTMessage htMessage) {
        String content = "";
        //附加字段json
        int action = htMessage.getIntAttribute("action", 0);
        if (action != 0) {
            String groupName = htMessage.getStringAttribute("groupName");
            String uid = htMessage.getStringAttribute("uid");
            String nickName = htMessage.getStringAttribute("nickName");

            switch (action) {
                case 2000:
                    content = "群聊 " + "\"" + groupName + "\"" + " 创建成功";
                    break;
                case 2001:
                    if (HTApp.getInstance().getUsername().equals(uid)) {
                        content = "你 修改了群资料";
                    } else {
                        content = "\"" + nickName + "\"" + " 修改了群资料";
                    }
                    break;
                case 2003:
                    HTMessageTextBody body = (HTMessageTextBody) htMessage.getBody();
                    content = body.getContent();
                    break;
                case 2004:
                    content = "\"" + nickName + "\"" + " 被移除群聊";
                    break;

                case 6001:
                    String opId = htMessage.getStringAttribute("opId");
                    if (TextUtils.isEmpty(opId)) {
                        String userNick = htMessage.getStringAttribute("nick");
                        content = userNick + "撤回了一条消息";
                    } else {
                        String msgFrom = htMessage.getFrom();
                        if (opId.equals(msgFrom)) {
                            //如果操作者和消息发出者是同一个人
                            if (opId.equals(UserManager.get().getMyUserId())) {
                                content = "你撤回了一条消息";
                            } else {
                                content = UserManager.get().getUserNick(opId) + "撤回了一条消息";
                            }
                        } else {
                            //操作者和发出者不是同一个人，只能说明是管理员撤回了别人的消息
                            if (opId.equals(UserManager.get().getMyUserId())) {
                                content = "你撤回了" + UserManager.get().getUserNick(msgFrom) + "消息";
                            } else {
                                if (msgFrom.equals(UserManager.get().getMyUserId())) {
                                    content = "管理员撤回了你的消息";
                                } else {
                                    content = "管理员撤回了" + UserManager.get().getUserNick(msgFrom) + "的消息";

                                }
                            }

                        }

                    }


                    break;
                case 10004:
                    String redFrom = htMessage.getStringAttribute("msgFrom");
                    String msgFrom = htMessage.getFrom();
                    if (TextUtils.isEmpty(redFrom) || TextUtils.isEmpty(msgFrom)) {
                        LoggerUtils.e("redFrom--->" + htMessage.toXmppMessageBody());
                        return "红包已被领取";
                    }
                    if (msgFrom.equals(UserManager.get().getMyUserId())) {
                        //我是领取者，看发送者是谁
                        if (redFrom.equals(UserManager.get().getMyUserId())) {
                            //我又是发送者
                            content = "你领取了自己的红包";
                        } else {

                            content = "你领取了" + UserManager.get().getUserNick(redFrom) + "的红包";
                        }
                    } else {

                        if (redFrom.equals(UserManager.get().getMyUserId())) {
                            //我又是发送者
                            content = UserManager.get().getUserNick(msgFrom) + "领取了你的红包";
                        } else {

                            content = UserManager.get().getUserNick(msgFrom) +

                                    "领取了" + UserManager.get().getUserNick(redFrom) + "的红包";
                        }

                    }

//
//                    HTMessageTextBody body1 = (HTMessageTextBody) htMessage.getBody();
//                    content = body1.getContent();
                    break;
                case 10005:
                    HTMessageTextBody body2 = (HTMessageTextBody) htMessage.getBody();
                    content = body2.getContent();
                    break;
            }
        }
        return content;
    }


}




 
