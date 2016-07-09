package com.fanxin.app.main.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fanxin.app.R;
import com.fanxin.app.main.activity.FXConstant;
import com.fanxin.app.main.activity.GroupAddMembersActivity;
import com.fanxin.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/9.\
 * QQ:84543217
 */

public class PickContactAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean[] isCheckedArray;
    private Bitmap[] bitmaps;
    private List<EaseUser> list = new ArrayList<EaseUser>();
    private GroupAddMembersActivity activity;
    private List<String> exitedMembers;

    public PickContactAdapter(GroupAddMembersActivity activity,
                              List<EaseUser> users, List<String> exitedMembers) {

        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = users;
        bitmaps = new Bitmap[list.size()];
        isCheckedArray = new boolean[list.size()];
        this.exitedMembers = exitedMembers;
    }

    public Bitmap getBitmap(int position) {
        return bitmaps[position];
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fx_item_contact_checkbox, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.tvNick = (TextView) convertView
                    .findViewById(R.id.tv_name);
            holder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            holder.tvHeader = (TextView) convertView
                    .findViewById(R.id.header);
        }
        EaseUser user = list.get(position);
        String avatar = user.getAvatar();
        String nick = user.getNick();
        String header = user.getInitialLetter();
        String hxid = user.getUsername();


        holder.tvNick.setText(nick);
        final ImageView ivAvatar = holder.ivAvatar;
        Glide.with(activity).load(FXConstant.URL_AVATAR + avatar).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ivAvatar.setImageBitmap(resource);
                bitmaps[position] = resource;
            }
        }); //方法中设置asBitmap可以设置回调类型

        if (position == 0 || header != null
                && !header.equals(getItem(position - 1))) {
            if ("".equals(header)) {
                holder.tvHeader.setVisibility(View.GONE);
            } else {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {
            holder.tvHeader.setVisibility(View.GONE);
        }
        if (exitedMembers != null && exitedMembers.contains(hxid)) {
            holder.checkBox.setButtonDrawable(R.drawable.fx_bg_checkbox);
        } else {
            holder.checkBox.setButtonDrawable(R.drawable.fx_bg_checkbox_blue);
        }
        return convertView;
    }

    @Override
    public int getCount() {
         return list.size();
    }

    @Override
    public EaseUser getItem(int position) {

        return  list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ViewHolder {
        private CheckBox checkBox;
        private ImageView ivAvatar;
        private TextView tvNick;
        private TextView tvHeader;
    }
}



