package com.fanxin.app.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanxin.app.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * Created by huangfangyi on 2016/7/15.\
 * QQ:84543217
 */
public   @SuppressLint("InflateParams")
class MessageAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context context;
    private LayoutInflater inflater;

    public MessageAdapter(List<EMMessage> msgs, Context context_) {
        this.msgs = msgs;
        this.context = context_;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);
        int viewType = getItemViewType(position);

        if (convertView == null) {

             convertView=inflater.inflate(R.layout.fx_item_live_chat, parent,false);
        }
        ViewHolder holder= (ViewHolder) convertView.getTag();
        if(holder==null){
            holder=new ViewHolder();
            holder.tvNick= (TextView) convertView.findViewById(R.id.tv_nick);
            holder.tvContent= (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();


        holder.tvContent.setText(txtBody.getMessage());


        return convertView;
    }


    private static class ViewHolder{

        TextView  tvNick;
        TextView  tvContent;
    }
}