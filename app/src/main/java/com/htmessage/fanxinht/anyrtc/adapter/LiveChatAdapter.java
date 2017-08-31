package com.htmessage.fanxinht.anyrtc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.anyrtc.Config.ChatMessageBean;

import java.util.List;

public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ChatListHolder> {

    String TAG = this.getClass().getSimpleName();
    List<ChatMessageBean> chatMessageList;
    Context context;


    public LiveChatAdapter(List<ChatMessageBean> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;
    }


    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_live_chat, parent, false);
        ChatListHolder holder = new ChatListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatListHolder holder, int position) {
        final ChatMessageBean chatMessageBean = chatMessageList.get(position);
        holder.txtChatName.setText(chatMessageBean.getmCustomName());
        Glide.with(context).load(chatMessageBean.getmCustomHeader()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(holder.imgHeader);
        if(chatMessageBean.getmMsgContent().equals("")) {
            holder.txtSpace.setVisibility(View.GONE);
            holder.txtChatMessage.setTextColor(R.color.icon_press);
            holder.txtChatMessage.setText(chatMessageBean.getmCustomID() + context.getString(R.string.str_online));
        } else {
//            holder.imgHeader.setVisibility(View.GONE);
            holder.txtChatMessage.setText(chatMessageBean.getmMsgContent());
        }
//        holder.llItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, UserDetailsActivity.class);
//                intent.putExtra(FXConstant.JSON_KEY_HXID, chatMessageBean.getmCustomName());
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();

    }

    public static class ChatListHolder extends RecyclerView.ViewHolder {
        ImageView imgHeader;
        TextView txtChatName;
        TextView txtSpace;
        TextView txtChatMessage;
        LinearLayout llItem;

        public ChatListHolder(View itemView) {
            super(itemView);
            imgHeader = (ImageView) itemView.findViewById(R.id.img_chat_header);
            txtChatName = (TextView) itemView.findViewById(R.id.txt_chat_name);
            txtSpace =  (TextView) itemView.findViewById(R.id.txt_space);
            txtChatMessage = (TextView) itemView.findViewById(R.id.txt_chat_message);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_itemt);
        }
    }

}
