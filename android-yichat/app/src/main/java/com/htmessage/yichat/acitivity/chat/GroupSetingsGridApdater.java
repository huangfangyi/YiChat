package com.htmessage.yichat.acitivity.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.group.ChatSettingGroupActivity;
import com.htmessage.yichat.acitivity.chat.group.deletemember.DeleteGroupMemberActivity;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.widget.HTAlertDialog;

/**
 * Created by huangfangyi on 2016/10/8.
 * qq 84543217
 */

public class GroupSetingsGridApdater extends BaseAdapter {
    private Context context;
    private JSONArray datas;
    private boolean isOwner;

    private String groupId;

    public GroupSetingsGridApdater(Context context, JSONArray datas, boolean isOwner, String groupId) {
        this.isOwner = isOwner;
        this.datas = datas;
        this.context = context;
        this.groupId = groupId;
    }

    @Override
    public int getCount() {
        if (isOwner) {
            return datas.size() + 2;
        } else {
            return datas.size() + 1;
        }


    }

    @Override
    public JSONObject getItem(int position) {
        if (position < datas.size()) {
            return datas.getJSONObject(position);
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
            convertView.setTag(holder);
        }
        holder.badgeDeleteView.setVisibility(View.INVISIBLE);

        if (position < datas.size()) {
            final JSONObject user = getItem(position);
            final String username = user.getString("userId");
            String userNick = UserManager.get().getUserNick(username);
            holder.textView.setText(userNick);
            String avatar = user.getString("avatar");
            UserManager.get().loadUserAvatar(context, avatar, holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOwner) {

                        new HTAlertDialog(context, null, new String[]{"查看资料", "禁言"}).init(new HTAlertDialog.OnItemClickListner() {
                            @Override
                            public void onClick(int position) {
                                switch (position) {
                                    case 0:
                                        context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("userId", username));

                                        break;
                                    case 1:
                                        GroupInfoManager.getInstance().addSilentUsers(groupId, username);
                                        break;
                                }


                            }
                        });


                    }


                }
            });
        } else if (position == datas.size()) {
            Glide.with(context).load("").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon_chat_add).into(holder.imageView);
            holder.textView.setText("");
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOwner) {
                        ((ChatSettingGroupActivity) context).startAddMembers();
                    }
                }
            });


        } else {
            holder.imageView.setImageResource(R.drawable.icon_chat_remove);
            //   Glide.with(context).load("").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon_chat_remove).into(holder.imageView);
            holder.textView.setText("");
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        isInDeleteMode = true;
//                        notifyDataSetChanged();
                    if (isOwner) {
                        context.startActivity(new Intent(context, DeleteGroupMemberActivity.class).putExtra("groupId", groupId));
                    }
                }
            });

        }

        return convertView;
    }


    private static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageView badgeDeleteView;
    }


}


