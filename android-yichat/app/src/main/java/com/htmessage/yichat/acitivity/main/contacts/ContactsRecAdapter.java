package com.htmessage.yichat.acitivity.main.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 项目名称：ktz
 * 类描述：ContactsRecAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2018/6/27 14:12
 * 邮箱:814326663@qq.com
 */
public class ContactsRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    private final static int HEAD_COUNT = 1;
    private final static int FOOT_COUNT = 1;
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    private final static int TYPE_FOOTER = 2;
    private Context context;
    private List<User> userList = new ArrayList<>();
    private OnItemClickListener listener;
    private TopHolder topHolder;
    private BottomHolder bottomHolder;

    public ContactsRecAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public boolean isHead(int position) {
        return HEAD_COUNT != 0 && position == 0;
    }

    public boolean isFoot(int position) {
        return FOOT_COUNT != 0 && position == userList.size() + HEAD_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int contentSize = userList.size();
        if (HEAD_COUNT != 0 && position == 0) { // 头部
            return TYPE_HEAD;
        } else if (FOOT_COUNT != 0 && position == HEAD_COUNT + contentSize) { // 尾部
            return TYPE_FOOTER;
        } else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View view = View.inflate(context, R.layout.item_contact_list_header, null);
            return new TopHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = View.inflate(context, R.layout.item_contact_list_footer, null);
            return new BottomHolder(view);
        } else {
            View view = View.inflate(context, R.layout.item_contact_list, null);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TopHolder) {
            topHolder = (TopHolder) holder;
            topHolder.re_newfriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopClick(1);
                    }
                }
            });
            topHolder.re_chatroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopClick(2);
                    }
                }
            });
            topHolder.re_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopClick(3);
                    }
                }
            });
            topHolder.re_public.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopClick(4);
                    }
                }
            });
            topHolder.re_sevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopClick(5);
                    }
                }
            });
        } else if (holder instanceof ItemHolder) {
            final int realPosition = position - 1;
            final User user = userList.get(realPosition);
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.tvHeader.setVisibility(View.VISIBLE);
            String nick = user.getNick();
            String avatar = user.getAvatar();
            String headerLetter = user.getInitialLetter();
            String preHeaderLetter = getPreHeaderLeeter(realPosition);
              if (headerLetter.equals(preHeaderLetter)) {
                itemHolder.tvHeader.setVisibility(View.GONE);
            } else {
                itemHolder.tvHeader.setVisibility(View.VISIBLE);
                itemHolder.tvHeader.setText(headerLetter);
            }


            itemHolder.nameTextview.setText(nick);
            UserManager.get().loadUserAvatar(context, avatar, itemHolder.iv_avatar);
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(realPosition, user);
                    }
                }
            });
            itemHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onItemLongClick(realPosition, user);
                    }
                    return true;
                }
            });
        } else if (holder instanceof BottomHolder) {
            bottomHolder = (BottomHolder) holder;
            showAllUser(userList.size());
            bottomHolder.tv_total.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBottomClick();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size() + FOOT_COUNT + HEAD_COUNT;
    }

    public int getRealItemCount() {
        return userList.size();
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        if (section != 42) {
            for (int i = 0; i < getRealItemCount(); i++) {
                String sortStr = userList.get(i).getInitialLetter();
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
        return userList.get(position).getInitialLetter().charAt(0);
    }


    public class TopHolder extends RecyclerView.ViewHolder {
        private RelativeLayout re_newfriends, re_chatroom, re_tag, re_public, re_sevice;
        private TextView tv_unread;

        public TopHolder(View itemView) {
            super(itemView);
            tv_unread = (TextView) itemView.findViewById(R.id.tv_unread);
            re_newfriends = (RelativeLayout) itemView.findViewById(R.id.re_newfriends);
            re_chatroom = (RelativeLayout) itemView.findViewById(R.id.re_chatroom);
            re_tag = (RelativeLayout) itemView.findViewById(R.id.re_tag);
            re_public = (RelativeLayout) itemView.findViewById(R.id.re_public);
            re_sevice = (RelativeLayout) itemView.findViewById(R.id.re_sevice);
        }
    }

    /**
     * 设置是否显示红点
     *
     * @param count
     */
    public void setUnReadText(int count) {
        if (topHolder != null) {
            if (count != 0) {
                topHolder.tv_unread.setVisibility(View.VISIBLE);
                topHolder.tv_unread.setText(count+"");
            } else {
                topHolder.tv_unread.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置是否显示红点
     *
     * @param count
     */
    public void showAllUser(int count) {
        if (bottomHolder != null) {
            bottomHolder.tv_total.setText(String.format(context.getString(R.string.more_people), String.valueOf(count)));
        }
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView nameTextview, tvHeader;

        public ItemHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.header);
            nameTextview = (TextView) itemView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

    public class BottomHolder extends RecyclerView.ViewHolder {
        private TextView tv_total;

        public BottomHolder(View itemView) {
            super(itemView);
            tv_total = (TextView) itemView.findViewById(R.id.tv_total);
        }
    }

    public String getPreHeaderLeeter(int position) {
        if (position > 0 && userList != null) {
            User user = userList.get(position - 1);
            return user.getInitialLetter();
        }
        return null;
    }

    public interface OnItemClickListener {
        void onTopClick(int type);

        void onItemClick(int position, User user);

        void onItemLongClick(int position, User user);

        void onBottomClick();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
