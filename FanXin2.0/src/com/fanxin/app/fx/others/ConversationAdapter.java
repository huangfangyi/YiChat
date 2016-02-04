package com.fanxin.app.fx.others;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.db.InviteMessgeDao;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.ChatActivity;
import com.fanxin.app.fx.MainActivity;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;
import com.fanxin.app.utils.SmileUtils;
import com.easemob.util.DateUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

@SuppressLint("InflateParams")
public class ConversationAdapter extends BaseAdapter {
    private List<EMConversation> normal_list;
    private List<EMConversation> top_list;
    private LayoutInflater inflater;
    private LoadUserAvatar avatarLoader;
    private Context context;
    Map<String, TopUser> topMap;

    @SuppressLint("SdCardPath")
    public ConversationAdapter(Context context,
            List<EMConversation> normal_list, List<EMConversation> top_list,
            Map<String, TopUser> topMap) {
        this.context = context;
        this.topMap = topMap;
        this.normal_list = normal_list;
        this.top_list = top_list;
        inflater = LayoutInflater.from(context);
        avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
    }

    @Override
    public int getCount() {
        return normal_list.size() + top_list.size();
    }

    @Override
    public EMConversation getItem(int position) {

        if (position < top_list.size()) {
            return top_list.get(position);

        } else {
            return normal_list.get(position - top_list.size());
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        // 获取与此用户/群组的会话
        final EMConversation conversation = getItem(position);
        // 获取用户username或者群组groupid
        final String username = conversation.getUserName();
        List<EMGroup> groups = EMGroupManager.getInstance().getAllGroups();

        boolean isGroup = false;
        String nick = "";
        String groupName = "";
        String[] avatars = new String[5];
        int membersNum = 0;
        for (EMGroup group : groups) {
            if (group.getGroupId().equals(username)) {
                isGroup = true;

                String groupName_temp = group.getGroupName();

                JSONObject jsonObject = JSONObject.parseObject(groupName_temp);
                JSONArray jsonarray = jsonObject.getJSONArray("jsonArray");
                groupName = jsonObject.getString("groupname");

                String groupName_temp2 = "";
                membersNum = jsonarray.size();

                for (int i = 0; i < membersNum; i++) {
                    JSONObject json = (JSONObject) jsonarray.get(i);
                    if (i < 5) {
                        avatars[i] = json.getString("avatar");
                        Log.e("avatars[i]----->>>", avatars[i]);
                    }

                    if (i == 0) {
                        groupName_temp2 = json.getString("nick");
                    } else if (i < 4) {
                        groupName_temp2 += "、" + json.getString("nick");

                    } else if (i == 4) {
                        groupName_temp2 += "。。。";
                    }
                }

                if (groupName.equals("未命名")) {
                    groupName = groupName_temp2;
                }

                break;
            }
        }

        //
        // convertView= inflater.inflate(R.layout.item_conversation_single,
        // parent, false);

        convertView = creatConvertView(membersNum);
        // // 单聊对话membersNum
        // creatConvertView(convertView, parent, membersNum);
        // Log.e("membersNum",String.valueOf(membersNum));
        // 初始化控件
        // 昵称
        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        // 未读消息
        holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
        // 最近一条消息
        holder.tv_content = (TextView) convertView
                .findViewById(R.id.tv_content);
        // 时间
        holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        // 发送状态

        holder.msgState = (ImageView) convertView.findViewById(R.id.msg_state);
        // 单聊数据加载
        if (!isGroup) {

            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            // 从好友列表中加载该用户的资料
            User user = MYApplication.getInstance().getContactList()
                    .get(username);
            if (user != null) {
                nick = user.getNick();
                String avatar = user.getAvatar();
                // 显示昵称
                holder.tv_name.setText(nick);
                // 显示头像
                showUserAvatar(holder.iv_avatar, avatar);
            }else{
                EMMessage message=conversation.getLastMessage();
                if(message.direct==EMMessage.Direct.RECEIVE){
                    try {
                        nick=message.getStringAttribute("myUserNick");
                        String avatar=message.getStringAttribute("myUserAvatar");
                        // 显示昵称
                        holder.tv_name.setText(nick);
                        // 显示头像
                        showUserAvatar(holder.iv_avatar, avatar);
                        
                    } catch (EaseMobException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                   
                }else{
                    try {
                        nick=message.getStringAttribute("toUserNick");
                        String avatar=message.getStringAttribute("toUserAvatar");
                        // 显示昵称
                        holder.tv_name.setText(nick);
                        // 显示头像
                        showUserAvatar(holder.iv_avatar, avatar);
                        
                    } catch (EaseMobException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
        // 群聊对话
        else {

            holder.tv_name.setText(groupName);

            if (membersNum == 1) {

                holder.iv_avatar1 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar1);

                showUserAvatar(holder.iv_avatar1, avatars[0]);

            } else if (membersNum == 2) {

                holder.iv_avatar1 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar1);
                holder.iv_avatar2 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar2);

                showUserAvatar(holder.iv_avatar1, avatars[0]);

                showUserAvatar(holder.iv_avatar2, avatars[1]);
            } else if (membersNum == 3) {

                holder.iv_avatar1 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar1);
                holder.iv_avatar2 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar2);
                holder.iv_avatar3 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar3);

                showUserAvatar(holder.iv_avatar1, avatars[0]);

                showUserAvatar(holder.iv_avatar2, avatars[1]);

                showUserAvatar(holder.iv_avatar3, avatars[2]);
            } else if (membersNum == 4) {

                holder.iv_avatar1 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar1);
                holder.iv_avatar2 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar2);
                holder.iv_avatar3 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar3);
                holder.iv_avatar4 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar4);

                showUserAvatar(holder.iv_avatar1, avatars[0]);

                showUserAvatar(holder.iv_avatar2, avatars[1]);

                showUserAvatar(holder.iv_avatar3, avatars[2]);

                showUserAvatar(holder.iv_avatar4, avatars[3]);

            } else if (membersNum > 4) {

                holder.iv_avatar1 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar1);
                holder.iv_avatar2 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar2);
                holder.iv_avatar3 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar3);
                holder.iv_avatar4 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar4);
                holder.iv_avatar5 = (ImageView) convertView
                        .findViewById(R.id.iv_avatar5);

                showUserAvatar(holder.iv_avatar1, avatars[0]);

                showUserAvatar(holder.iv_avatar2, avatars[1]);

                showUserAvatar(holder.iv_avatar3, avatars[2]);

                showUserAvatar(holder.iv_avatar4, avatars[3]);

                showUserAvatar(holder.iv_avatar5, avatars[4]);

            }

        }
        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.tv_unread.setText(String.valueOf(conversation
                    .getUnreadMsgCount()));
            holder.tv_unread.setVisibility(View.VISIBLE);
        } else {
            holder.tv_unread.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.tv_content.setText(
                    SmileUtils.getSmiledText(context,
                            getMessageDigest(lastMessage, context)),
                    BufferType.SPANNABLE);

            holder.tv_time.setText(DateUtils.getTimestampString(new Date(
                    lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND
                    && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }
        final String groupName_temp = groupName;
        final boolean isGroup_temp = isGroup;
        final String nick_temp = nick;
        RelativeLayout re_parent = (RelativeLayout) convertView
                .findViewById(R.id.re_parent);

        re_parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (username
                        .equals(MYApplication.getInstance().getUserName()))
                    Toast.makeText(context, "不能和自己聊天...", Toast.LENGTH_SHORT)
                            .show();

                else {
                    // 进入聊天页面
                    Intent intent = new Intent(context, ChatActivity.class);
                    if (isGroup_temp) {
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
                        intent.putExtra("groupName", groupName_temp);
                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                        intent.putExtra("userNick", nick_temp);
                    }
                    context.startActivity(intent);
                }

            }

        });

        re_parent.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String title = "";

                if (isGroup_temp) {
                    title = groupName_temp;
                } else {

                    title = nick_temp;
                }

                showMyDialog(title, conversation);

                return false;
            }
        });
        if (position < top_list.size()) {
            // 加入删除后
            re_parent.setBackgroundColor(0xFFF5FFF1);

        }
        return convertView;
    }

    private View creatConvertView(int size) {
        View convertView;

        if (size == 0) {
            convertView = inflater.inflate(R.layout.item_conversation_single,
                    null, false);
        } else if (size == 1) {
            convertView = inflater.inflate(R.layout.item_conversation_group1,
                    null, false);

        } else if (size == 2) {
            convertView = inflater.inflate(R.layout.item_conversation_group2,
                    null, false);

        } else if (size == 3) {
            convertView = inflater.inflate(R.layout.item_conversation_group3,
                    null, false);

        } else if (size == 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group4,
                    null, false);

        } else if (size > 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group5,
                    null, false);

        } else {
            convertView = inflater.inflate(R.layout.item_conversation_group5,
                    null, false);

        }

        return convertView;
    }

    private static class ViewHolder {
        /** 和谁的聊天记录 */
        TextView tv_name;
        /** 消息未读数 */
        TextView tv_unread;
        /** 最后一条消息的内容 */
        TextView tv_content;
        /** 最后一条消息的时间 */
        TextView tv_time;
        /** 用户头像 */
        ImageView iv_avatar;
        ImageView iv_avatar1;
        ImageView iv_avatar2;
        ImageView iv_avatar3;
        ImageView iv_avatar4;
        ImageView iv_avatar5;
        ImageView msgState;

    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
        final String url_avatar = Constant.URL_Avatar + avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);

        }
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     * 
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct == EMMessage.Direct.RECEIVE) {

                digest = getStrng(context, R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
                // digest = EasyUtils.getAppResourceString(context,
                // "location_prefix");
                digest = getStrng(context, R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息

            digest = getStrng(context, R.string.picture);

            break;
        case VOICE:// 语音消息
            digest = getStrng(context, R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = getStrng(context, R.string.video);
            break;
        case TXT: // 文本消息
            if (!message.getBooleanAttribute(
                    Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            } else {
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = getStrng(context, R.string.voice_call)
                        + txtBody.getMessage();
            }
            break;
        case FILE: // 普通文件消息
            digest = getStrng(context, R.string.file);
            break;
        default:
            System.err.println("error, unknow type");
            return "";
        }

        return digest;
    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    private void showMyDialog(String title, final EMConversation conversation) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();

        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);

        window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
        final String username = conversation.getUserName();
        // 是否已经置顶

        if (topMap.containsKey(username)) {
            tv_content1.setText("取消置顶");

        } else {
            tv_content1.setText("置顶聊天");

        }

        tv_content1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {

                if (topMap.containsKey(username)) {

                    topMap.remove(username);
                    TopUserDao topUserDao = new TopUserDao(context);

                    topUserDao.deleteTopUser(username);

                } else {
                    TopUser topUser = new TopUser();
                    topUser.setTime(System.currentTimeMillis());
                    // 1---表示是群组
                    topUser.setType(1);
                    topUser.setUserName(username);
                    Map<String, TopUser> map = new HashMap<String, TopUser>();
                    map.put(conversation.getUserName(), topUser);
                    topMap.putAll(map);
                    TopUserDao topUserDao = new TopUserDao(context);
                    topUserDao.saveTopUser(topUser);

                }
                ((MainActivity) context).homefragment.refresh();
                dlg.cancel();
            }
        });
        TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
        tv_content2.setText("删除该聊天");
        tv_content2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EMChatManager.getInstance().deleteConversation(
                        conversation.getUserName(), conversation.isGroup());
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(context);
                inviteMessgeDao.deleteMessage(conversation.getUserName());

                ((MainActivity) context).homefragment.refresh();

                dlg.cancel();

            }
        });

    }

}
