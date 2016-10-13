package com.fanxin.huangfangyi.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends BaseAdapter {

    private static final int TYPE_1 = 1;
    private static final int TYPE_2 = 2;
    private static final int TYPE_3 = 3;
    private static final int TYPE_4 = 4;
    private static final int TYPE_5 = 5;
    private static final int TYPE_6 = 6;
    private static final int TYPE_7 = 7;
    private static final int TYPE_8 = 8;
    private static final int TYPE_9 = 9;

    private Context context;
    private List<EMGroup> grouplist;
    private LayoutInflater inflater;

    public GroupsAdapter(Context context, List<EMGroup> grouplist) {
        this.context = context;
        this.grouplist = grouplist;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return grouplist.size();
    }

    @Override
    public EMGroup getItem(int position) {


        return grouplist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        EMGroup group = grouplist.get(position);
        String groupName_temp = group.getGroupName();
        JSONArray jsonarray = new JSONArray();
        try {
            JSONObject jsonObject = JSONObject.parseObject(groupName_temp);
            jsonarray = jsonObject.getJSONArray("jsonArray");
        } catch (JSONException e) {
        }
        int num=jsonarray.size();
        if(num==0){
            num=1;

        }else if(num>9){

            num=9;
        }
        return num;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Log.d("type-->>",type+"");
        EMGroup group = grouplist.get(position);
        JSONArray jsonarray = new JSONArray();
        String groupName = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(group.getGroupName());
            jsonarray = jsonObject.getJSONArray("jsonArray");
            groupName = jsonObject.getString("groupname");
        } catch (JSONException e) {
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fx_item_groups, parent, false);
            RelativeLayout avatarView = (RelativeLayout) convertView.findViewById(R.id.re_avatar);
            avatarView.addView(creatAvatarView(type));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {

            holder = new ViewHolder();
            switch (type) {
                case TYPE_1:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    break;
                case TYPE_2:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                case TYPE_3:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                case TYPE_4:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                case TYPE_5:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);

                case TYPE_6:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                    holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                case TYPE_7:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                    holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                    holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                case TYPE_8:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                    holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                    holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                    holder.iv_avatar8 = (ImageView) convertView.findViewById(R.id.iv_avatar8);
                case TYPE_9:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    holder.iv_avatar2 = (ImageView) convertView.findViewById(R.id.iv_avatar2);
                    holder.iv_avatar3 = (ImageView) convertView.findViewById(R.id.iv_avatar3);
                    holder.iv_avatar4 = (ImageView) convertView.findViewById(R.id.iv_avatar4);
                    holder.iv_avatar5 = (ImageView) convertView.findViewById(R.id.iv_avatar5);
                    holder.iv_avatar6 = (ImageView) convertView.findViewById(R.id.iv_avatar6);
                    holder.iv_avatar7 = (ImageView) convertView.findViewById(R.id.iv_avatar7);
                    holder.iv_avatar8 = (ImageView) convertView.findViewById(R.id.iv_avatar8);
                    holder.iv_avatar9 = (ImageView) convertView.findViewById(R.id.iv_avatar9);
                    break;
                default:
                    holder.iv_avatar1 = (ImageView) convertView.findViewById(R.id.iv_avatar1);
                    break;

            }
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }

        List<String> avatars = new ArrayList<>();
        //用户名拼接的群名称，用于群名称未被修改的情况
        String groupNameTemp = "";
        //   List<String> nicks = new ArrayList<>();
        for (int i = 0; i < jsonarray.size(); i++) {
            try {
                JSONObject userJson = jsonarray.getJSONObject(i);
                avatars.add(userJson.getString("avatar"));
                if (i == 0) {
                    groupNameTemp = userJson.getString("nick");
                } else if (i < 4) {
                    groupNameTemp += "、" + userJson.getString("nick");
                } else if (i == 4) {
                    groupNameTemp += "...";
                }
            } catch (JSONException e) {
            }
            if (i > 8) break;
        }
        if (groupName.equals("未命名")) {
            groupName = groupNameTemp;
        }
        holder.tv_name.setText(groupName);
        if (jsonarray.size() != 0) {
            switch (type) {
                case TYPE_1:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                     break;
                case TYPE_2:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    break;
                case TYPE_3:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    break;
                case TYPE_4:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    break;
                case TYPE_5:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    break;
                case TYPE_6:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    break;
                case TYPE_7:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    break;
                case TYPE_8:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(7)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar8);
                    break;
                case TYPE_9:
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar1);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar2);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar3);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(3)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar4);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(4)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar5);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(5)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar6);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(6)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar7);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(7)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar8);
                    Glide.with(context).load(FXConstant.URL_AVATAR + avatars.get(8)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.iv_avatar9);
                    break;

            }

        }


        return convertView;
    }

    private static class ViewHolder {
        TextView tv_name;
        ImageView iv_avatar1;
        ImageView iv_avatar2;
        ImageView iv_avatar3;
        ImageView iv_avatar4;
        ImageView iv_avatar5;
        ImageView iv_avatar6;
        ImageView iv_avatar7;
        ImageView iv_avatar8;
        ImageView iv_avatar9;


    }


    private View creatAvatarView(int type) {

        switch (type) {
            case 1:
                return inflater.inflate(R.layout.fx_group_avatar1, null,
                        false);

            case 2:
                return inflater.inflate(R.layout.fx_group_avatar2, null,
                        false);

            case 3:
                return inflater.inflate(R.layout.fx_group_avatar3, null,
                        false);

            case 4:
                return inflater.inflate(R.layout.fx_group_avatar4, null,
                        false);
            case 5:
                return inflater.inflate(R.layout.fx_group_avatar5, null,
                        false);

            case 6:
                return inflater.inflate(R.layout.fx_group_avatar6, null,
                        false);
            case 7:
                return inflater.inflate(R.layout.fx_group_avatar7, null,
                        false);
            case 8:
                return inflater.inflate(R.layout.fx_group_avatar8, null,
                        false);
            case 9:
                return inflater.inflate(R.layout.fx_group_avatar9, null,
                        false);
            default:
                return inflater.inflate(R.layout.fx_group_avatar1, null,
                        false);
        }

    }

}
