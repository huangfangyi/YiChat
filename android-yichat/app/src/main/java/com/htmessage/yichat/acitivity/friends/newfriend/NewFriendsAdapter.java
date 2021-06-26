package com.htmessage.yichat.acitivity.friends.newfriend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.update.data.UserManager;

public class NewFriendsAdapter extends RecyclerView.Adapter<NewFriendsAdapter.ViewHolder> {
    private Context context;
    private JSONArray data;

    public NewFriendsAdapter(Context _context, JSONArray data) {
        this.context = _context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_newfriend_msg, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        JSONObject jsonObject = data.getJSONObject(position);
        final String fid = jsonObject.getString("fid");
        final String avatar = jsonObject.getString("avatar");
        final String userId = jsonObject.getString("userId");
        int status = jsonObject.getInteger("status");
        String nick = jsonObject.getString("nick");
        UserManager.get().saveUserNickAvatar(userId, nick, avatar);

        holder.tv_name.setText(nick);
        UserManager.get().loadUserAvatar(context, avatar, holder.iv_avatar);
        if (status == 0) {
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.tv_added.setVisibility(View.GONE);
        } else if (status == 1) {
            holder.btn_add.setVisibility(View.GONE);
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.tv_added.setText(R.string.has_contro);
        } else if (status == 2) {

            holder.btn_add.setVisibility(View.GONE);
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.tv_added.setText(R.string.apply_ok);
        }

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeListener != null) {
                    v.setTag(fid);
                    agreeListener.onClicked(fid, position, userId);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               if(onItemLongClickListener!=null){
                  return onItemLongClickListener.onItemLongClick(v,position);
               }

                return true;
            }
        });
    }

    private AgreeListener agreeListener;
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }


    public void setAgreeListener(AgreeListener agreeListener) {
        this.agreeListener = agreeListener;
    }

    public interface AgreeListener {
        void onClicked(String fid, int position, String userId);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_reason;
        private TextView tv_added;
        private Button btn_add;
        private RelativeLayout rl_item_add;

        public ViewHolder(View convertView) {
            super(convertView);
            iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
            tv_added = (TextView) convertView.findViewById(R.id.tv_added);
            btn_add = (Button) convertView.findViewById(R.id.btn_add);
            rl_item_add = (RelativeLayout) convertView.findViewById(R.id.rl_item_add);
        }
    }


}
