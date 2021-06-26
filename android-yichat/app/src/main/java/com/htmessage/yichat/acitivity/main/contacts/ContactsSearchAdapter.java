package com.htmessage.yichat.acitivity.main.contacts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.update.data.UserManager;

import java.util.List;
import java.util.Locale;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class ContactsSearchAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<User> data;

    public ContactsSearchAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_list, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
            holder.nameTextview = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);

            convertView.setTag(holder);
        }

        User user = data.get(position);

        holder.tvHeader.setVisibility(View.VISIBLE);
        String nick = user.getNick();
        String avatar = user.getAvatar();
        String headerLetter = user.getInitialLetter();
        String preHeaderLetter = getPreHeaderLeeter(position);
          if (headerLetter.equals(preHeaderLetter)) {
            holder.tvHeader.setVisibility(View.GONE);
        } else {
            holder.tvHeader.setVisibility(View.VISIBLE);
            holder.tvHeader.setText(headerLetter);
        }
        holder.nameTextview.setText(nick);
        UserManager.get().loadUserAvatar(context, avatar, holder.iv_avatar);
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;

    }

    @Override
    public int getPositionForSection(int section) {
        if (section != 42) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = data.get(i).getInitialLetter();
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
        return data.get(position).getInitialLetter().charAt(0);
    }

    private class ViewHolder {
        ImageView iv_avatar;
        TextView nameTextview, tvHeader;
    }

    private String getPreHeaderLeeter(int position) {
        if (position > 0) {
            User user = getItem(position - 1);
            return user.getInitialLetter();
        }
        return null;

    }

}
