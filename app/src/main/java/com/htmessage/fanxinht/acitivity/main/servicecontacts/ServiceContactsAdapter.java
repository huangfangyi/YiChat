package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.R;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ServiceContactsAdapter extends BaseAdapter{
    private Context context;
    private List<ServiceUser> data;

    public ServiceContactsAdapter(Context context, List<ServiceUser> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ServiceUser getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_list, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
            holder.nameTextview = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }

        ServiceUser user = data.get(position);
        holder.tvHeader.setVisibility(View.VISIBLE);
        String nick = user.getNick();
        String avatar = user.getAvatar();
        String headerLetter = user.getInitialLetter();
        String preHeaderLetter = getPreHeaderLeeter(position);
        if (headerLetter.equals(preHeaderLetter)) {
            holder.tvHeader.setVisibility(View.GONE);
        } else {
            holder.tvHeader.setVisibility(View.VISIBLE);
            holder.tvHeader.setText(headerLetter);
        }
        holder.nameTextview.setText(nick);
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(avatar).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(holder.iv_avatar);
        }

        int unReadMsgCount = getUnReadMsgCount(user.getUsername());
        if (unReadMsgCount != 0) {
            holder.tv_count.setVisibility(View.VISIBLE);
            holder.tv_count.setText(String.valueOf(unReadMsgCount));
        } else {
            holder.tv_count.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
    private class ViewHolder {
        ImageView iv_avatar;
        TextView nameTextview, tvHeader, tv_count;
    }

    private String getPreHeaderLeeter(int position) {
        if (position > 0) {
            ServiceUser user = getItem(position - 1);
            return user.getInitialLetter();
        }
        return null;

    }

    private int getUnReadMsgCount(String groupId) {
        int unreadCount = 0;
        List<HTConversation> conversations = HTClient.getInstance().conversationManager().getAllConversations();
        if (conversations != null && conversations.size() != 0) {
            for (int i = 0; i < conversations.size(); i++) {
                HTConversation conversation = conversations.get(i);
                String userId = conversation.getUserId();
                if (groupId.equals(userId)) {
                    unreadCount = conversation.getUnReadCount();
                }
            }
        }
        return unreadCount;
    }
}
