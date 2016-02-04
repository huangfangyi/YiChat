package com.fanxin.app.fx.others;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMGroup;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.fx.ChatActivity;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "SdCardPath", "InflateParams" })
public class ChatRoomAdapter extends BaseAdapter {

    Context context;
    List<EMGroup> grouplist;
    private LayoutInflater inflater;
    private LoadUserAvatar avatarLoader;

    public ChatRoomAdapter(Context context, List<EMGroup> grouplist) {
        this.context = context;
        this.grouplist = grouplist;
        inflater = LayoutInflater.from(context);
        avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        EMGroup group = grouplist.get(position);
        String groupName_temp = group.getGroupName();
        final String groupId = group.getGroupId();
       
        int membersNum = 0;

        JSONObject jsonObject = JSONObject.parseObject(groupName_temp);
        JSONArray jsonarray = jsonObject.getJSONArray("jsonArray");
        String groupName = jsonObject.getString("groupname");

        String groupName_temp2 = "";
        membersNum = jsonarray.size();
        convertView = creatConvertView(membersNum);
        String[] avatars = new String[membersNum];
        for (int i = 0; i < membersNum; i++) {
            JSONObject json = (JSONObject) jsonarray.get(i);
            if(i<5){
                avatars[i] = json.getString("avatar");              
            }
            if (i == 0) {
                groupName_temp2 = json.getString("nick");
            } else if (i < 4) {
                groupName_temp2 += "、" + json.getString("nick");

            } else if (i == 4) {
                groupName_temp2 += "...";
            }
        }

        if (groupName.equals("未命名")) {
            groupName = groupName_temp2;
        }

        if (membersNum == 1) {
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_name.setText(groupName);
            holder.iv_avatar1 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar1);
            showUserAvatar(holder.iv_avatar1, avatars[0]);
        } else if (membersNum == 2) {
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_name.setText(groupName);
            holder.iv_avatar1 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar1);

            holder.iv_avatar2 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar2);
            showUserAvatar(holder.iv_avatar1, avatars[0]);
            showUserAvatar(holder.iv_avatar2, avatars[1]);
        } else if (membersNum == 3) {
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_name.setText(groupName);
            holder.iv_avatar1 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar1);

            holder.iv_avatar2 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar2);
            holder.iv_avatar3 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar3);
            showUserAvatar(holder.iv_avatar3, avatars[2]);
            showUserAvatar(holder.iv_avatar1, avatars[0]);
            showUserAvatar(holder.iv_avatar2, avatars[1]);
        } else if (membersNum == 4) {
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_name.setText(groupName);
            holder.iv_avatar1 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar1);

            holder.iv_avatar2 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar2);
            holder.iv_avatar3 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar3);
            holder.iv_avatar4 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar4);
            showUserAvatar(holder.iv_avatar4, avatars[3]);
            showUserAvatar(holder.iv_avatar3, avatars[2]);
            showUserAvatar(holder.iv_avatar1, avatars[0]);
            showUserAvatar(holder.iv_avatar2, avatars[1]);
        } else if (membersNum > 4) {
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_name.setText(groupName);
            holder.iv_avatar1 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar1);

            holder.iv_avatar2 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar2);
            holder.iv_avatar3 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar3);
            holder.iv_avatar4 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar4);
            holder.iv_avatar5 = (ImageView) convertView
                    .findViewById(R.id.iv_avatar5);
            showUserAvatar(holder.iv_avatar5, avatars[4]);
            showUserAvatar(holder.iv_avatar4, avatars[3]);
            showUserAvatar(holder.iv_avatar3, avatars[2]);
            showUserAvatar(holder.iv_avatar1, avatars[0]);
            showUserAvatar(holder.iv_avatar2, avatars[1]);
        }
      final String  groupName_tem=groupName;
        // 为了item变色在此处写监听
        RelativeLayout re_item = (RelativeLayout) convertView
                .findViewById(R.id.re_item);
        re_item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 进入群聊
                Intent intent = new Intent(context, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName_tem);
                context.startActivity(intent);
            }

        });

        return convertView;
    }

    private static class ViewHolder {

        TextView tv_name;
        ImageView iv_avatar1;
        ImageView iv_avatar2;
        ImageView iv_avatar3;
        ImageView iv_avatar4;
        ImageView iv_avatar5;

    }
 
    private void showUserAvatar(ImageView iamgeView, String avatar) {
        final String url_avatar = Constant.URL_Avatar + avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);

        }
    }

    private View creatConvertView( int size) {
        View convertView;
        switch (size) {
        case 1:
            convertView = inflater.inflate(R.layout.item_chatroom_1, null,
                    false);

            break;
        case 2:
            convertView = inflater.inflate(R.layout.item_chatroom_2, null,
                    false);
            break;
        case 3:
            convertView = inflater.inflate(R.layout.item_chatroom_3, null,
                    false);
            break;
        case 4:
            convertView = inflater.inflate(R.layout.item_chatroom_4, null,
                    false);
            break;
        case 5:
            convertView = inflater.inflate(R.layout.item_chatroom_5, null,
                    false);
        default:
            convertView = inflater.inflate(R.layout.item_chatroom_5, null,
                    false);
            break;

        }
        return convertView;
    }

}
