package com.htmessage.yichat.acitivity.main.conversation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class SmallProgramGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<JSONObject> objectList = new ArrayList<>();

    public SmallProgramGridViewAdapter(Context mContext, List<JSONObject> objectList) {
        this.mContext = mContext;
        this.objectList = objectList;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return objectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_small_program, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final JSONObject object = objectList.get(position);
        String smallIcon = object.getString("icon");
        String smallName = object.getString("title");
        holder.tv_small_name.setText(smallName);
        CommonUtils.loadNumalUrlIcon(mContext, smallIcon, holder.iv_small_icon);
        return convertView;
    }

    private class ViewHolder {

        private ImageView iv_small_icon;
        private TextView tv_small_name;

        public ViewHolder(View itemView) {
            iv_small_icon = (ImageView) itemView.findViewById(R.id.iv_small_icon);
            tv_small_name = (TextView) itemView.findViewById(R.id.tv_small_name);
        }
    }

}
