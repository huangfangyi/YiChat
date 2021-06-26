package com.htmessage.yichat.acitivity.red;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：CityBz
 * 类描述：RpHistoryAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/10 13:10
 * 邮箱:814326663@qq.com
 */
public class RpDetailsAdapter extends BaseAdapter {
    private JSONArray rpList;
    private Context context;

    public RpDetailsAdapter(JSONArray rpList, Context context) {
        this.context = context;
        this.rpList = rpList;
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
            convertView = View.inflate(context, R.layout.rp_group_details, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject object = getItem(position);
        String name = object.getString("userId");
        String nick = object.getString("nick");
        String avatar = object.getString("avatar");
        String money = object.getString("money");

        if (object.getInteger("maxStatus")==1) {
            holder.tv_rp_end.setVisibility(View.VISIBLE);
            holder.iv_zuijia.setVisibility(View.VISIBLE);
            holder.tv_rp_end.setText("手气最佳");
        } else {

            holder.tv_rp_end.setVisibility(View.INVISIBLE);
            holder.iv_zuijia.setVisibility(View.INVISIBLE);
        }


        holder.tv_rp_time.setVisibility(View.VISIBLE);

        String receiveTime = object.getString("receiveTime");

        holder.tv_rp_name.setText(nick);
        holder.tv_rp_money.setText(String.format(parent.getContext().getString(R.string.rp_money_yuan), Validator.formatMoney(money)));
        holder.tv_rp_time.setText(receiveTime);
        UserManager.get().loadUserAvatar(context, avatar, holder.iv_avatar);

        return convertView;
    }


    private class ViewHolder {
        private TextView tv_rp_name, tv_rp_money, tv_rp_time, tv_rp_end;
        private ImageView iv_avatar, iv_zuijia;

        public ViewHolder(View view) {
            tv_rp_name = (TextView) view.findViewById(R.id.tv_rp_name);
            tv_rp_money = (TextView) view.findViewById(R.id.tv_rp_money);
            tv_rp_time = (TextView) view.findViewById(R.id.tv_rp_time);
            tv_rp_end = (TextView) view.findViewById(R.id.tv_rp_end);
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            iv_zuijia = (ImageView) view.findViewById(R.id.iv_zuijia);
        }
    }
}
