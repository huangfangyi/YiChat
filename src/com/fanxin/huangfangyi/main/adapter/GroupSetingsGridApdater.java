package com.fanxin.huangfangyi.main.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.activity.ChatSettingGroupActivity;
import com.fanxin.huangfangyi.main.activity.UserDetailsActivity;
import com.fanxin.huangfangyi.main.utils.GroupUitls;
import com.hyphenate.chat.EMClient;

import java.util.List;

/**
 * Created by huangfangyi on 2016/10/8.
 * qq 84543217
 */

public class GroupSetingsGridApdater extends BaseAdapter {
    private Context context;
    private List<JSONObject> datas;
    private boolean isOwner;

    public boolean isInDeleteMode = false;

    public GroupSetingsGridApdater(Context context, List<JSONObject> datas, boolean isOwner) {
        this.isOwner = isOwner;
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (isOwner && isInDeleteMode) {

            return datas.size();
        } else if (isOwner && !isInDeleteMode) {

            return datas.size() + 2;
        } else if (!isOwner) {

            return datas.size() + 1;
        }
        return 0;

    }

    @Override
    public JSONObject getItem(int position) {
        if (position < datas.size()) {

            return datas.get(position);
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.em_grid, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
            convertView.setTag(holder);
        }
        final JSONObject jsonObject = getItem(position);

        if (jsonObject!=null) {
            final String username = jsonObject.getString(FXConstant.JSON_KEY_HXID);
            holder.textView.setText(jsonObject.getString(FXConstant.JSON_KEY_NICK));
          //  EaseUserUtils.setUserNick(username,);
         //   EaseUserUtils.setUserAvatar(context, username, holder.imageView);
            Glide.with(context).load(FXConstant.URL_AVATAR+jsonObject.getString(FXConstant.JSON_KEY_AVATAR)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UserDetailsActivity.class).putExtra(FXConstant.KEY_USER_INFO,jsonObject.toJSONString()));
                }
            });
        }

        if (isOwner && isInDeleteMode) {
            final String username = jsonObject.getString(FXConstant.JSON_KEY_HXID);
            holder.badgeDeleteView.setVisibility(View.VISIBLE);
            holder.badgeDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser(username);
                    isInDeleteMode = false;
                    notifyDataSetChanged();
                }
            });
        } else if (isOwner && !isInDeleteMode) {
            holder.badgeDeleteView.setVisibility(View.INVISIBLE);
            if (position == getCount() - 1) {
                Glide.with(context).load("").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.em_smiley_minus_btn).into(holder.imageView);
              //  holder.imageView.setImageResource(R.drawable.);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isInDeleteMode = true;
                        notifyDataSetChanged();
                    }
                });
            } else if (position == getCount() - 2) {
                Glide.with(context).load("").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.em_smiley_add_btn).into(holder.imageView);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ChatSettingGroupActivity)context).startAddMembers();
                    }
                });
            }
        } else if (!isOwner) {
            if (position == getCount() - 1) {
                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ChatSettingGroupActivity)context).startAddMembers();
                    }
                });
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageView badgeDeleteView;


    }

    private void deleteUser(final String hxid) {

        if(hxid.equals(DemoHelper.getInstance().getCurrentUsernName())){
            Toast.makeText(context,"不能删除自己",Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog deleteDialog = new ProgressDialog(context);
        deleteDialog.setMessage("正在删除...");
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 删除被选中的成员
                    EMClient.getInstance().groupManager().removeUserFromGroup( ((ChatSettingGroupActivity)context).groupId, hxid);

                   for(JSONObject jsonObject: datas){
                     if(jsonObject.getString("hxid").equals(hxid))  {

                         datas.remove(jsonObject);

                     }

                   }

                    GroupUitls.getInstance().checkGroupNameWhenDetele(((ChatSettingGroupActivity)context).group.getGroupName(),((ChatSettingGroupActivity)context).groupId,hxid);
                    isInDeleteMode = false;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deleteDialog.dismiss();
                            notifyDataSetChanged();
                            ((ChatSettingGroupActivity)context).refreshMembers();

//                                ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
//                                        + group.getAffiliationsCount() + st);
                        }
                    });
                } catch (final Exception e) {

                    ((Activity)context).runOnUiThread(new Runnable() {
                        public void run() {
                            deleteDialog.dismiss();
                            Toast.makeText(context, "删除错误：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();


    }


}


