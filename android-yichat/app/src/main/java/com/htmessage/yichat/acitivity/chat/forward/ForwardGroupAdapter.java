package com.htmessage.yichat.acitivity.chat.forward;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.sdk.model.HTGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目名称：yichat
 * 类描述：GroupCheckAdapter 描述: 转发选择群组的适配器
 * 创建人：songlijie
 * 创建时间：2017/3/18 17:27
 * 邮箱:814326663@qq.com
 */
public class ForwardGroupAdapter extends BaseAdapter {
    private Context mContext;
    private List<HTGroup> beans = new ArrayList<>();

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    public ForwardGroupAdapter(Context mContext, List<HTGroup> beans) {
        this.mContext = mContext;
        this.beans = beans;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }
    @Override
    public int getCount() {
        return beans.size();
    }
    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < beans.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public HTGroup getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.layout_group_check,null);
            holder = new ViewHolder();
            holder.re_avatar = (ImageView) convertView.findViewById(R.id.re_avatar);
            holder.checkbox =  (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        HTGroup group = beans.get(position);
        holder.tv_name.setText(group.getGroupName());
        // 根据isSelected来设置checkbox的选中状况
        holder.checkbox.setChecked(getIsSelected().get(position));
        CommonUtils.loadGroupAvatar(mContext,group.getImgUrl(),holder.re_avatar);
        return convertView;
    }

    public static class ViewHolder{
        TextView tv_name;
        ImageView re_avatar;
        public CheckBox checkbox;
    }
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
}
