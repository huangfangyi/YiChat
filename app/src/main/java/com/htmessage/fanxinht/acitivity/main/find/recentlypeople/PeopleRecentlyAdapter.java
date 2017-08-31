package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 17:17
 * 邮箱:814326663@qq.com
 */
public class PeopleRecentlyAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> peoples = new ArrayList<>();

    public PeopleRecentlyAdapter(Context context, List<JSONObject> peoples) {
        this.context = context;
        this.peoples = peoples;
    }

    @Override
    public int getCount() {
        return peoples.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return peoples.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_people_recently, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject = peoples.get(position);
        String nick = jsonObject.getString(HTConstant.JSON_KEY_NICK);
        if (TextUtils.isEmpty(nick)) {
            nick = jsonObject.getString(HTConstant.JSON_KEY_HXID);
        }
        String recentlytime = jsonObject.getString("recentlyTime");
        String serviceTime = jsonObject.getString("serviceTime");
        long l = Long.valueOf(recentlytime) * 1000;
        long l2 = Long.valueOf(serviceTime) * 1000;
        String difference = DateUtils.getTimeDifference(parent.getContext(),l, l2);
        String avatar = null;
        if (jsonObject.containsKey(HTConstant.JSON_KEY_AVATAR)) {
            avatar = jsonObject.getString(HTConstant.JSON_KEY_AVATAR);
            if (!TextUtils.isEmpty(avatar)) {
                if (!avatar.contains("http")) {
                    avatar = HTConstant.baseImgUrl + avatar;
                }
            }
        }
        String sex = jsonObject.getString(HTConstant.JSON_KEY_SEX);
        if (!TextUtils.isEmpty(sex)) {
            if ("1".equals(sex) || sex.equals(parent.getContext().getString(R.string.male))) {
                holder.iv_sex.setImageResource(R.drawable.icon_male);
            } else if ("0".equals(sex) || sex.equals(parent.getContext().getString(R.string.female))) {
                holder.iv_sex.setImageResource(R.drawable.icon_female);
            }
        } else {
            holder.iv_sex.setImageResource(R.drawable.icon_male);
        }
        String sign = jsonObject.getString(HTConstant.JSON_KEY_SIGN);
        if (!TextUtils.isEmpty(sign)) {
            holder.ll_sign.setVisibility(View.VISIBLE);
            holder.tv_sign.setText(sign);
        } else {
            holder.ll_sign.setVisibility(View.GONE);
        }
        holder.tv_name.setText(nick);
        holder.tv_time.setText(difference);//DateUtils.getStringTime(Long.valueOf(recentlytime) * 1000)
        Glide.with(parent.getContext()).load(avatar).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_avatar);
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_name, tv_time, tv_sign;
        private ImageView iv_avatar, iv_sex;
        private LinearLayout ll_sign;

        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_sign = (TextView) view.findViewById(R.id.tv_sign);
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
            ll_sign = (LinearLayout) view.findViewById(R.id.ll_sign);
        }
    }
}
