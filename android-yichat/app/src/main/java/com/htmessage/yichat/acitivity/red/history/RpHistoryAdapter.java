package com.htmessage.yichat.acitivity.red.history;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：CityBz
 * 类描述：RpHistoryAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/10 13:10
 * 邮箱:814326663@qq.com
 */
public class RpHistoryAdapter extends BaseAdapter {
    private JSONArray rpList ;
    private Context context;
    private boolean isRec = false;

    public RpHistoryAdapter(JSONArray rpList, Context context, boolean isRec) {
        this.context = context;
        this.rpList = rpList;
         this.isRec = isRec;
    }

    @Override
    public int getCount() {
        return rpList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return rpList.getJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_rp_history, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject data = getItem(position);
        if(isRec){
            holder.tv_rp_name.setText(data.getString("nick"));
        }else {
            //发出显示
            if(data.getInteger("type")==1){
                holder.tv_rp_name.setText("拼手气红包");

            }else {
                holder.tv_rp_name.setText("普通红包");

            }
            int totalCount=data.getInteger("totalCount");
            int receiveCount=data.getInteger("receiveCount");
            int status=data.getInteger("status");
            holder.tv_rp_status.setVisibility(View.VISIBLE);
            String content;
            if(status==2){
                content="已领完"+totalCount+"/"+totalCount+"个";
            }else if(status==3){
                content="已过期"+receiveCount+"/"+totalCount+"个";
            }else {
                content=+receiveCount+"/"+totalCount+"个";

            }
            holder.tv_rp_status.setText(content);
        }

        holder.tv_rp_money.setText(String.format(parent.getContext().getString(R.string.rp_money_yuan), Validator.formatMoney(data.getString("money"))));
        holder.tv_rp_time.setText(data.getString("receiveTime"));
        if(data.getInteger("type")==1){
            holder.iv_group.setVisibility(View.VISIBLE);

        }else {
            holder.iv_group.setVisibility(View.INVISIBLE);

        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_rp_name, tv_rp_money, tv_rp_time,tv_rp_status ;
        private ImageView iv_group;

        public ViewHolder(View view) {
            tv_rp_name = (TextView) view.findViewById(R.id.tv_rp_name);
            tv_rp_money = (TextView) view.findViewById(R.id.tv_rp_money);
            tv_rp_time = (TextView) view.findViewById(R.id.tv_rp_time);
            iv_group= (ImageView) view.findViewById(R.id.iv_group);
            tv_rp_status= (TextView) view.findViewById(R.id.tv_rp_status);
          }
    }
}
