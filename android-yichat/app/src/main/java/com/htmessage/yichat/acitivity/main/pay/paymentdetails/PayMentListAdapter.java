package com.htmessage.yichat.acitivity.main.pay.paymentdetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentListAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 15:57
 * 邮箱:814326663@qq.com
 */
public class PayMentListAdapter extends RecyclerView.Adapter<PayMentListAdapter.ViewHolder> {
    private OnPayMentListClickListener listClickListener;
    private List<JSONObject> objectList = new ArrayList<>();
    private Context context;

    public PayMentListAdapter(Context context, List<JSONObject> objectList) {
        this.objectList = objectList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_pay_logs, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final JSONObject object = objectList.get(position);
        String time = object.getString("time");
        String nickname = object.getString("nickname");
        String userId = object.getString("userId");
        String avatar = object.getString("avatar");
        String note = object.getString("note");
        String amount = object.getString("amount");
        if (TextUtils.isEmpty(nickname)) {
            nickname = userId;
        }
        if (TextUtils.isEmpty(avatar)) {
            avatar = userId;
        }
        if (TextUtils.isEmpty(note)) {
            holder.tv_content.setText(R.string.from_pay_log_scan);
        } else {
            holder.tv_content.setText(String.format(context.getString(R.string.from_pay_log), note));
        }
        holder.tv_nick.setText(nickname);
        holder.tv_money.setText(amount);
        holder.tv_time.setText(time);
        UserManager.get().loadUserAvatar(context, avatar, holder.iv_avatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listClickListener != null) {
                    listClickListener.onPaymentClick(position, object);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_nick, tv_time, tv_content, tv_money;
        private ImageView iv_avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nick = (TextView) itemView.findViewById(R.id.tv_nick);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

    public void setListClickListener(OnPayMentListClickListener listClickListener) {
        this.listClickListener = listClickListener;
    }

    public interface OnPayMentListClickListener {
        void onPaymentClick(int positon, JSONObject object);
    }

}
