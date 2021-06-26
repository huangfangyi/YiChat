package com.htmessage.yichat.acitivity.chat.group.allmember;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：fanxinht
 * 类描述：AllGroupMemberAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/21 16:48
 * 邮箱:814326663@qq.com
 */
public class AllGroupMemberAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> beans = new ArrayList<>();

    public AllGroupMemberAdapter(Context mContext, List<User> beans) {
        this.mContext = mContext;
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public User getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_group_check, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = beans.get(position);
        String userNick = UserManager.get().getUserNick(user.getUserId());
         holder.tv_name.setText(userNick);
        holder.checkbox.setVisibility(View.GONE);
        // 根据isSelected来设置checkbox的选中状况
        UserManager.get().loadUserAvatar(mContext, user.getAvatar(), holder.re_avatar);
        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_name;
        public ImageView re_avatar;
        public CheckBox checkbox;

        public ViewHolder(View View) {
            re_avatar = (ImageView) View.findViewById(R.id.re_avatar);
            checkbox = (CheckBox) View.findViewById(R.id.checkbox);
            tv_name = (TextView) View.findViewById(R.id.tv_name);
        }
    }
}
