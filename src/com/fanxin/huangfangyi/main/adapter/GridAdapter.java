package com.fanxin.huangfangyi.main.adapter;

/**
 * Created by huangfangyi on 2016/7/10.\
 * QQ:84543217
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.activity.GroupAddMembersActivity;
import com.fanxin.huangfangyi.main.activity.UserDetailsActivity;
import com.fanxin.easeui.domain.EaseUser;

import java.util.List;

/**
 * 群组成员gridadapter
 *
 * @author admin_new
 */
public class GridAdapter extends BaseAdapter {

    public boolean isInDeleteMode;
    private List<EaseUser> users;
    private Context context;
    private boolean isAdmin = false;
    private static final int ITEM_USER = 1;
    private static final int ITEM_ADD = 2;
    private static final int ITEM_DEL = 3;
    private String groupId;
    public GridAdapter(Context context, List<EaseUser> users, boolean isAdmin,String groupId) {
        this.isAdmin = isAdmin;
        this.users = users;
        this.context = context;
        isInDeleteMode = false;
        this.groupId=groupId;
    }

    @Override
    public int getCount() {
        if (isAdmin) {
            return users.size() + 2;
        } else {
            return users.size() + 1;
        }

    }

    @Override
    public Object getItem(int position) {

        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.fx_item_grid_user, null);
        }
        ViewHolder holder= (ViewHolder) convertView.getTag();
        if(holder==null){
            holder.ivAvatar= (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.ivDel=(ImageView) convertView
                    .findViewById(R.id.iv_delete);
            holder.tvNick=(TextView) convertView
                    .findViewById(R.id.tv_nick);
            convertView.setTag(holder);
        }

        // 最后一个item，减人按钮
        if (position == getCount() - 1 && isAdmin) {
            holder.tvNick.setText("");
            holder.ivDel.setVisibility(View.GONE);
            holder.ivAvatar.setImageResource(R.drawable.fx_icon_delete);
            if (isInDeleteMode) {
                // 正处于删除模式下，隐藏删除按钮
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
            }

            holder.ivAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    isInDeleteMode = true;
                    notifyDataSetChanged();
                }

            });

        } else if ((isAdmin && position == getCount() - 2)
                || (!isAdmin && position == getCount() - 1)) { // 添加群组成员按钮
             holder.tvNick.setText("");
            holder.ivDel.setVisibility(View.GONE);
            holder.ivAvatar.setImageResource(R.drawable.fx_icon_add);
            // 正处于删除模式下,隐藏添加按钮
            if (isInDeleteMode) {
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
            }
            holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入选人页面
                    context.startActivity((new Intent(context, GroupAddMembersActivity.class).putExtra("groupId", groupId)));
                }
            });
        } else { // 普通item，显示群组成员

            final EaseUser user = users.get(position);
            String usernick = user.getNick();
            final String userhid = user.getUsername();
            final String useravatar = user.getAvatar();
            holder.tvNick.setText(usernick);
            holder.ivAvatar.setImageResource(R.drawable.fx_default_useravatar);


            // demo群组成员的头像都用默认头像，需由开发者自己去设置头像
            if (isInDeleteMode) {
                // 如果是删除模式下，显示减人图标
                convertView.findViewById(R.id.iv_delete).setVisibility(
                        View.VISIBLE);
            } else {
                convertView.findViewById(R.id.iv_delete).setVisibility(
                        View.INVISIBLE);
            }
            holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInDeleteMode) {
                        // 如果是删除自己，return
                        if (DemoHelper.getInstance().getCurrentUsernName()
                                .equals(userhid)) {
                            return;
                        }
                   //     deleteMembersFromGroup(userhid);
                    } else {
                         //正常情况下点击user，可以进入用户详情或者聊天页面等等
                         context.startActivity(new Intent(context, UserDetailsActivity.class).putExtra(FXConstant.KEY_USER_INFO,user.getUserInfo()));
                    }
                }

            });

        }
        return convertView;
    }

    private static class ViewHolder {
        private ImageView ivDel;
        private ImageView ivAvatar;
        private TextView tvNick;

    }

}

