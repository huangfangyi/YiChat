package com.fanxin.huangfangyi.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by huangfangyi on 2016/7/15.\
 * QQ:84543217
 */
public class LiveMessageAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context context;
    private LayoutInflater inflater;

    public LiveMessageAdapter(List<EMMessage> msgs, Context context_) {
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


     @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);


        if (convertView == null) {

            convertView = inflater.inflate(R.layout.fx_item_live_chat, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tvNick = (TextView) convertView.findViewById(R.id.tv_nick);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();

        try {
            String userInfo = message.getStringAttribute(FXConstant.KEY_USER_INFO);
            JSONObject jsonObject = JSONObject.parseObject(userInfo);
            holder.tvNick.setText(jsonObject.getString(FXConstant.JSON_KEY_NICK));
        } catch (HyphenateException e) {
            holder.tvNick.setText(message.getFrom());
            e.printStackTrace();
        } catch (JSONException e) {

            holder.tvNick.setText(message.getFrom());
            e.printStackTrace();
        }
        holder.tvContent.setText(txtBody.getMessage());


        return convertView;
    }


    private static class ViewHolder {

        TextView tvNick;
        TextView tvContent;
    }
}