package com.htmessage.fanxinht.acitivity.addfriends.invitefriend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.htmessage.fanxinht.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ContactsInviteAdapter extends RecyclerView.Adapter<ContactsInviteAdapter.ViewHolder> implements SectionIndexer, View.OnClickListener {
    private Context context;
    private List<ContactInfo> data;
    private List<String> list;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ContactsInviteAdapter(Context context, List<ContactInfo> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_invite_contacts, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactInfo user = data.get(position);
        Log.d("slj", "------shuju:" + user.getLetter() + "-----hehe:" + user.getUserId());
        String info = user.getId();
        String nick = user.getName();
        String number = user.getPhoneNumber();
//        number = number.replace(number.substring(3, 7), "****");
        int type = user.getType();
        String headerLetter = user.getLetter();
        String preHeaderLetter = getPreHeaderLeeter(position);
        if (headerLetter.equals(preHeaderLetter)) {
            holder.tvHeader.setVisibility(View.GONE);
        } else {
            holder.tvHeader.setVisibility(View.VISIBLE);
            holder.tvHeader.setText(headerLetter);
            if ("*".equals(headerLetter)) {
                holder.tvHeader.setText(R.string.add_contacts_friends);
            }
        }
        holder.nameTextview.setText(nick);
        holder.tv_number.setText(number);
        holder.btn_invite.setOnClickListener(this);
        holder.itemView.setOnClickListener(this);
        holder.btn_invite.setTag(user);
        holder.itemView.setTag(user);
        switch (type) {
            case 1://未注册
                holder.btn_invite.setText(R.string.check_again);
                holder.btn_invite.setBackground(context.getDrawable(R.drawable.bg_btn_green));
                break;
            case 2://不是好友
                holder.btn_invite.setText(R.string.add);
                holder.btn_invite.setBackground(context.getDrawable(R.drawable.invite_bg_btn_red));
                break;
            case 3://是好友
                holder.btn_invite.setText(R.string.already_add);
                holder.btn_invite.setBackground(context.getDrawable(R.drawable.invite_bg_btn_gray));
                break;
        }
//        GlideUtils.downLoadRoundTransform(context,avatar,holder.iv_avatar);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getItemCount();
        list = new ArrayList<String>();
        list.add("*");
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            String letter = data.get(i).getLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);

    }


    public int getPositionForSection(int section) {
        return positionOfSection.get(section) + 1;
    }

    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_invite) {
            if (listener != null) {
                listener.onInviteButtonClick(v, (ContactInfo) v.getTag());
            }
        } else {
            if (listener != null) {
                listener.onItemClick(v, (ContactInfo) v.getTag());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ContactInfo info);

        void onInviteButtonClick(View view, ContactInfo info);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView nameTextview, tvHeader, tv_number, tv_add_status;
        private Button btn_invite;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.header);
            nameTextview = (TextView) itemView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            btn_invite = (Button) itemView.findViewById(R.id.btn_invite);
            tv_add_status = (TextView) itemView.findViewById(R.id.tv_add_status);
        }
    }

    private String getPreHeaderLeeter(int position) {
        if (position > 0) {
            ContactInfo user = data.get(position - 1);
            return user.getLetter();
        }
        return null;

    }
}
