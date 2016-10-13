package com.fanxin.huangfangyi.main.adapter;

/**
 * Created by huangfangyi on 2016/7/13.\
 * QQ:84543217
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.easeui.domain.EaseUser;
import com.fanxin.easeui.model.EaseAtMessageHelper;
import com.fanxin.easeui.utils.EaseCommonUtils;
import com.fanxin.easeui.utils.EaseSmileUtils;
import com.fanxin.easeui.utils.EaseUserUtils;
import com.fanxin.easeui.widget.EaseConversationList;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * conversation list adapter
 */
public class ConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;
    private static final int TYPE_0 = 0;
    private static final int TYPE_1 = 1;
    private static final int TYPE_2 = 2;
    private static final int TYPE_3 = 3;
    private static final int TYPE_4 = 4;
    private static final int TYPE_5 = 5;
    private static final int TYPE_6 = 6;
    private static final int TYPE_7 = 7;
    private static final int TYPE_8 = 8;
    private static final int TYPE_9 = 9;

    public ConversationAdapter(Context context, int resource,
                               List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getType() == EMConversation.EMConversationType.GroupChat) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(getItem(position).getUserName());
            JSONArray jsonarray = new JSONArray();
            if(group!=null){
                String groupName_temp = group.getGroupName();

                try {
                    JSONObject jsonObject = JSONObject.parseObject(groupName_temp);
                    jsonarray = jsonObject.getJSONArray("jsonArray");
                } catch (JSONException e) {
                }
            }
            int num=jsonarray.size();
            if(num==0){
                num=1;

            }else if(num>9){

                num=9;
            }

            return num;

        } else {
            return 0;
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         int type=getItemViewType(position);
        if (convertView == null) {
            convertView=   getViewByType(type,parent);
          //  convertView = LayoutInflater.from(getContext()).inflate(com.hyphenate.easeui.R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            if(type!=0){
                switch (type) {
                    case TYPE_1:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        break;
                    case TYPE_2:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    case TYPE_3:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    case TYPE_4:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    case TYPE_5:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                        holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);

                    case TYPE_6:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                        holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                        holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                    case TYPE_7:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                        holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                        holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                        holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                    case TYPE_8:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                        holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                        holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                        holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                        holder.iv_avatar8 = (ImageView) convertView.findViewById(R.id.iv_avatar8);
                    case TYPE_9:
                        holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                        holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                        holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                        holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                        holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                        holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                        holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                        holder.iv_avatar8 = (ImageView) convertView.findViewById(R.id.iv_avatar8);
                        holder.iv_avatar9 = (ImageView) convertView.findViewById(R.id.iv_avatar9);
                        break;
                }

            }else{

                holder.avatar = (ImageView) convertView.findViewById(com.hyphenate.easeui.R.id.avatar);
            }

            holder.name = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.message);
            holder.time = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.time);

            holder.msgState = convertView.findViewById(com.hyphenate.easeui.R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(com.hyphenate.easeui.R.id.list_itease_layout);
            holder.motioned = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.mentioned);
            convertView.setTag(holder);
        }
        holder.list_itease_layout.setBackgroundResource(com.hyphenate.easeui.R.drawable.ease_mm_listitem);

        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String username = conversation.getUserName();

        if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
            String groupId = conversation.getUserName();
            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar

           // holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            holdGroupAvatar(group,holder,type);
          //  holder.name.setText(group != null ? group.getGroupName() : username);
        } else if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
            holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
        } else {
            EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
            EaseUserUtils.setUserNick(username, holder.name);
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // show unread message count
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() != 0) {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if (cvsListHelper != null) {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                    TextView.BufferType.SPANNABLE);
            if (content != null) {
                holder.message.setText(content);
            }
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

//        //set property
//        holder.name.setTextColor(primaryColor);
//        holder.message.setTextColor(secondaryColor);
//        holder.time.setTextColor(timeColor);
//        if (primarySize != 0)
//            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
//        if (secondarySize != 0)
//            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
//        if (timeSize != 0)
//            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();

                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    } else {
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        // TODO: not support Nick anymore
//                        if(user != null && user.getNick() != null)
//                            username = user.getNick();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        TextView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        /**
         * avatar
         */
        ImageView avatar;
        /**
         * status of last message
         */
        View msgState;
        /**
         * layout
         */
        RelativeLayout list_itease_layout;
        TextView motioned;


        ImageView iv_avatar1;
        ImageView iv_avatar2;
        ImageView iv_avatar3;
        ImageView iv_avatar4;
        ImageView iv_avatar5;
        ImageView iv_avatar6;
        ImageView iv_avatar7;
        ImageView iv_avatar8;
        ImageView iv_avatar9;
    }

    private View getViewByType(int type, ViewGroup parent) {

        if (type == 0) {
            return LayoutInflater.from(getContext()).inflate(R.layout.fx_item_conversation_single, parent, false);
        } else {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.fx_item_conversation_group, parent, false);
            RelativeLayout avatarView = (RelativeLayout) view.findViewById(R.id.re_avatar);
            avatarView.addView(creatAvatarView(type));
            return view;
        }
    }

    private View creatAvatarView(int type) {

        switch (type) {
            case 1:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar1, null,
                        false);

            case 2:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar2, null,
                        false);

            case 3:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar3, null,
                        false);

            case 4:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar4, null,
                        false);
            case 5:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar5, null,
                        false);

            case 6:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar6, null,
                        false);
            case 7:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar7, null,
                        false);
            case 8:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar8, null,
                        false);
            case 9:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar9, null,
                        false);
            default:
                return LayoutInflater.from(getContext()).inflate(R.layout.fx_group_avatar1, null,
                        false);
        }

    }


    private void holdGroupAvatar(EMGroup group,ViewHolder holder,int type){
        if(group==null||group.getGroupName()==null){
            return;
        }
        JSONArray jsonarray = new JSONArray();
        String groupName = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(group.getGroupName());
            jsonarray = jsonObject.getJSONArray("jsonArray");
            groupName = jsonObject.getString("groupname");
        } catch (JSONException e ) {
            return;
        }

        List<String> avatars = new ArrayList<>();
        //用户名拼接的群名称，用于群名称未被修改的情况
        String groupNameTemp = "";
        //   List<String> nicks = new ArrayList<>();
        for (int i = 0; i < jsonarray.size(); i++) {
            try {
                JSONObject userJson = jsonarray.getJSONObject(i);
                avatars.add(userJson.getString("avatar"));
                if (i == 0) {
                    groupNameTemp = userJson.getString("nick");
                } else if (i < 4) {
                    groupNameTemp += "、" + userJson.getString("nick");
                } else if (i == 4) {
                    groupNameTemp += "...";
                }
            } catch (JSONException e) {
            }
            if (i > 8) break;
        }
        if (groupName.equals("未命名")) {
            groupName = groupNameTemp;
        }
        holder.name.setText(groupName);
        if (jsonarray.size() != 0) {
            switch (type) {
                case TYPE_1:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                     break;
                case TYPE_2:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    break;
                case TYPE_3:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    break;
                case TYPE_4:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    break;
                case TYPE_5:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    break;
                case TYPE_6:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    break;
                case TYPE_7:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    break;
                case TYPE_8:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(7)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar8);
                    break;
                case TYPE_9:
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(7)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar8);
                    Glide.with(getContext()).load(FXConstant.URL_AVATAR + avatars.get(8)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar9);
                    break;

            }

        }

    }
}

