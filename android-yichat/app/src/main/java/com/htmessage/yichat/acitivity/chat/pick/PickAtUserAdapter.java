package com.htmessage.yichat.acitivity.chat.pick;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public class PickAtUserAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<User> data;

    public PickAtUserAdapter(Context context, List<User> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

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

        final User user = data.get(position);
        holder.tvHeader.setVisibility(View.VISIBLE);
        String nick = user.getNick();
        String avatar = user.getAvatar();
        String headerLetter = user.getInitialLetter();
        String preHeaderLetter = getPreHeaderLeeter(position);
//        nick = CommonUtils.getRemark(user.getUserInfo(),nick); //TODO 暂时屏蔽 好友备注
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

    List<String> list;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(context.getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            System.err.println("contactadapter getsection getHeader:" + letter
                    + " name:" + getItem(i).getUserId());
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
