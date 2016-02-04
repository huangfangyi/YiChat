package com.fanxin.app.fx.others;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;

/**
 * 简单的好友Adapter实现
 * 
 */
public class ContactAdapter extends ArrayAdapter<User> implements
        SectionIndexer {

    List<String> list;
    List<User> userList;
    List<User> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    public MyFilter myFilter;
    private LoadUserAvatar avatarLoader;

    @SuppressLint("SdCardPath")
	public ContactAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<User>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
        avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
        }

        ImageView iv_avatar = (ImageView) convertView
                .findViewById(R.id.iv_avatar);

        TextView nameTextview = (TextView) convertView
                .findViewById(R.id.tv_name);
        TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
        View view_temp = (View) convertView.findViewById(R.id.view_temp);
        User user = getItem(position);
        if (user == null)
            Log.d("ContactAdapter", position + "");
        // 设置nick，demo里不涉及到完整user，用username代替nick显示

        String header = user.getHeader();
        String usernick = user.getNick();
        String useravatar = user.getAvatar();
        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                tvHeader.setVisibility(View.GONE);
                view_temp.setVisibility(View.VISIBLE);
            } else {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(header);
                view_temp.setVisibility(View.GONE);
            }
        } else {
            tvHeader.setVisibility(View.GONE);
            view_temp.setVisibility(View.VISIBLE);
        }
        // 显示申请与通知item

        nameTextview.setText(usernick);
        iv_avatar.setImageResource(R.drawable.default_useravatar);
        showUserAvatar(iv_avatar, useravatar);

        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getHeader();
            System.err.println("contactadapter getsection getHeader:" + letter
                    + " name:" + getItem(i).getUsername());
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

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    private class MyFilter extends Filter {
        List<User> mList = null;

        public MyFilter(List<User> myList) {
            super();
            this.mList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(
                CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mList == null) {
                mList = new ArrayList<User>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mList.size();
                final ArrayList<User> newValues = new ArrayList<User>();
                for (int i = 0; i < count; i++) {
                    final User user = mList.get(i);
                    String username = user.getUsername();

                    EMConversation conversation = EMChatManager.getInstance()
                            .getConversation(username);
                    if (conversation != null) {
                        username = conversation.getUserName();
                    }

                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with
                        // space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected synchronized void publishResults(CharSequence constraint,
                FilterResults results) {
            userList.clear();
            userList.addAll((List<User>) results.values);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
        if(avatar==null||avatar.equals("")) return;
        final String url_avatar = Constant.URL_Avatar + avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);

        }
    }

}
