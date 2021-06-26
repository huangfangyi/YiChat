package com.htmessage.yichat.acitivity.chat.group.deletemember;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.yichat.R;
import com.htmessage.update.data.UserManager;

import java.util.HashMap;

/**
 * 项目名称：fanxinht
 * 类描述：DeleteGroupMemberAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/21 16:48
 * 邮箱:814326663@qq.com
 */
public class DeleteGroupMemberAdapter extends BaseAdapter {
    private JSONArray beans;
    private Context mContext;
    // 用来控制CheckBox的选中状况
    private static HashMap<JSONObject, Boolean> isSelect = new HashMap<>();

    public DeleteGroupMemberAdapter(Context mContext, JSONArray beans) {
        this.mContext = mContext;
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return beans.getJSONObject(position);
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
        JSONObject user = getItem(position);
        String userId=user.getString("userId");
         String nick = UserManager.get().getUserNick(userId);
         holder.tv_name.setText(nick);
        // 根据isSelected来设置checkbox的选中状况
        holder.checkbox.setChecked(isSelect.containsKey(user) ? isSelect.get(user) : false);


        UserManager.get().loadUserAvatar(mContext, user.getString("avatar"), holder.re_avatar);
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

    public void addUser(JSONObject user) {
        if (isSelect != null) {
            if (!isSelect.containsKey(user)) {
                isSelect.put(user, true);
            }
        }
    }

    public void removeUser(JSONObject user) {
        if (isSelect != null) {
            if (isSelect.containsKey(user)) {
                isSelect.remove(user);
            }
        }
    }

    public void removeAllUser() {
        if (isSelect != null) {
            isSelect.clear();
        }
    }
}
