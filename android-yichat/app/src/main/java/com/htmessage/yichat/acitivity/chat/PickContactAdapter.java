package com.htmessage.yichat.acitivity.chat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.group.GroupAddMembersActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/9.\
 * QQ:84543217
 */

public class PickContactAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean[] isCheckedArray;
     private List<String> list = new ArrayList<>();
    private GroupAddMembersActivity activity;
    private List<String> exitedMembers;

    public PickContactAdapter(GroupAddMembersActivity activity,
                              List<String> users, List<String> exitedMembers) {

        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = users;
         isCheckedArray = new boolean[list.size()];
        this.exitedMembers = exitedMembers;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_contact_checkbox, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.tvNick = (TextView) convertView
                    .findViewById(R.id.tv_name);
            holder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            holder.tvHeader = (TextView) convertView
                    .findViewById(R.id.header);
            convertView.setTag(holder);
        }
        final String userId = list.get(position);
        String avatar = UserManager.get().getUserAvatar(userId);
        String nick = UserManager.get().getUserNick(userId);
        String header = User.getInitialLetter(nick);
        holder.tvNick.setText(nick);
        UserManager.get().loadUserAvatar(activity, avatar, holder.ivAvatar);
        //设置头像位置
         if (position == 0) {
            if ("".equals(header)) {
                holder.tvHeader.setVisibility(View.GONE);
            } else {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {
            String preHead = User.getInitialLetter(UserManager.get().getUserNick(getItem(position - 1)));
            if (!TextUtils.isEmpty(header) && !header.equals(preHead)) {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }else {
                holder.tvHeader.setVisibility(View.GONE);

            }

        }
        if (exitedMembers != null && exitedMembers.contains(userId)) {
            holder.checkBox.setButtonDrawable(R.drawable.bg_checkbox);
        } else {
            holder.checkBox.setButtonDrawable(R.drawable.bg_checkbox_blue);
        }
        holder.checkBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(position, userId));
        if (exitedMembers != null && exitedMembers.contains(userId)) {
        } else {
            holder.checkBox.setChecked(isCheckedArray[position]);
        }
        return convertView;
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private int positon;
        private String user;

        public MyOnCheckedChangeListener(int positon, String userId) {

            this.positon = positon;
            this.user = userId;
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (exitedMembers.contains(user)) {
                return;
            }
            if (isChecked) {
                activity.addTolist( user);
                isCheckedArray[positon] = true;
            } else {
                activity.removeFromList(user);
                isCheckedArray[positon] = false;
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ViewHolder {
        private CheckBox checkBox;
        private ImageView ivAvatar;
        private TextView tvNick;
        private TextView tvHeader;
    }
}



