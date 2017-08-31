package com.htmessage.fanxinht.acitivity.moments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.moments.details.MomentsDetailActivity;
import com.htmessage.fanxinht.domain.MomentsMessage;
import com.htmessage.fanxinht.domain.MomentsMessageDao;
import com.htmessage.fanxinht.utils.DateUtils;

import java.util.List;

/**
 * Created by huangfangyi on 2017/7/23.
 * qq 84543217
 */

public class MomentsNoticeActivity extends BaseActivity {

    private MomentsMessageDao messageDao;
    private NoticeAdapter adapter;
    private List<MomentsMessage> momentsMessages;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_momets_notice);
        messageDao = new MomentsMessageDao(this);
        momentsMessages = messageDao.getMomentsMessageList();
        setTitle(R.string.message);
        showRightTextView(getString(R.string.clear_message), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDao.deleteAllMomentsMessage();
                momentsMessages.clear();
                adapter.notifyDataSetChanged();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new NoticeAdapter(MomentsNoticeActivity.this, momentsMessages);
        messageDao.clearMomentsUnread();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MomentsMessage momentsMessage=adapter.getItem(position);
                startActivity(new Intent(MomentsNoticeActivity.this,MomentsDetailActivity.class).putExtra("mid",momentsMessage.getMid()));
            }
        });

        LocalBroadcastManager.getInstance(MomentsNoticeActivity.this).sendBroadcast(new Intent(IMAction.ACTION_MOMENTS_READ));

    }


    private class NoticeAdapter extends BaseAdapter {

        private List<MomentsMessage> data;

        private Context context;

        public NoticeAdapter(Context context, List<MomentsMessage> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public MomentsMessage getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_moments_notice, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.ivMoments = (ImageView) convertView.findViewById(R.id.iv_moments);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);
            }

            MomentsMessage momentsMessage = getItem(position);

            Glide.with(context).load(momentsMessage.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(holder.ivAvatar);

            Glide.with(context).load(HTConstant.baseImgUrl + momentsMessage.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).centerCrop().into(holder.ivMoments);
            holder.tvName.setText(momentsMessage.getUserNick());
            holder.tvTime.setText(DateUtils.getStringTime(momentsMessage.getTime()));
            if (momentsMessage.getType() == MomentsMessage.Type.GOOD) {
                holder.tvContent.setText("");
                holder.tvContent.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.icon_heart_blue),null,null,null);
                //holder.tvContent.setCompoundDrawables();

            } else {
                holder.tvContent.setText(momentsMessage.getContent());

            }


            return convertView;
        }
    }


    private class ViewHolder {

        ImageView ivAvatar;
        TextView tvName;
        TextView tvTime;
        ImageView ivMoments;
        TextView tvContent;


    }
}
