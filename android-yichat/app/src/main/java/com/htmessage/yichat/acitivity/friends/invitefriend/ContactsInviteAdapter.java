package com.htmessage.yichat.acitivity.friends.invitefriend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

 import com.htmessage.yichat.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ContactsInviteAdapter extends RecyclerView.Adapter<ContactsInviteAdapter.ViewHolder> implements SectionIndexer, View.OnClickListener {
    private Context context;
    private List<ContactInfo> data;
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

        }
        holder.nameTextview.setText(nick);
        holder.tv_number.setText(number);
         holder.itemView.setOnClickListener(this);
         holder.itemView.setTag(user);

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
        return null;

    }

    @Override
    public int getPositionForSection(int section) {
        if (section != 42) {
            for (int i = 0; i < getItemCount(); i++) {
                String sortStr = data.get(i).getLetter();
                char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        } else {
            return 0;
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return data.get(position).getLetter().charAt(0);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.btn_invite) {
//            if (listener != null) {
//                listener.onInviteButtonClick(v, (ContactInfo) v.getTag());
//            }
//        } else {
            if (listener != null) {
                listener.onItemClick(v, (ContactInfo) v.getTag());
            }
        //}
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ContactInfo info);

        void onInviteButtonClick(View view, ContactInfo info);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
         private TextView nameTextview, tvHeader, tv_number, tv_add_status;
          private ImageView iv_avatar;
        public ViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.header);
            nameTextview = (TextView) itemView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);

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
