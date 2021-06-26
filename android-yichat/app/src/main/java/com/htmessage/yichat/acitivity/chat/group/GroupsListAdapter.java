package com.htmessage.yichat.acitivity.chat.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.List;

/**
 * Created by huangfangyi on 2016/12/21.
 * qq 84543217
 */

public class GroupsListAdapter extends BaseAdapter {
    private List<HTGroup> htGroups;
    private Context context;
    public GroupsListAdapter(Context context, List<HTGroup> htGroups){
        this.htGroups=htGroups;
        this.context=context;

    }

    @Override
    public int getCount() {
        return htGroups.size();
    }

    @Override
    public HTGroup getItem(int i) {
        return htGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_groups,viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        HTGroup item = getItem(i);
        holder.textView.setText(item.getGroupName());
        CommonUtils.loadGroupAvatar(context,item.getImgUrl(),holder.iv_avatar);
      //  Glide.with(context).load(item.getImgUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_group).error(R.drawable.default_group).into(holder.iv_avatar);
        return view;
    }
    private  class ViewHolder{
        private ImageView iv_avatar;
        private TextView textView;
        public ViewHolder(View view){
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            textView= (TextView) view.findViewById(R.id.tv_name);
        }
    }

}
