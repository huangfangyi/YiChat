package com.htmessage.yichat.acitivity.moments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.moments.details.MomentsDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MomentsFriendAdapter extends BaseAdapter {

    private MomentsFriendActivity context;
    private List<JSONObject> data;
    private LayoutInflater inflater;

    private String recordDate = "";
    private String avatar;
    private int pId = 0;
     private ImageView iv_moment_bg;

    private String background;
    public MomentsFriendAdapter(MomentsFriendActivity context1, List<JSONObject> jsonArray, String avatar) {
        this.context = context1;
        this.data = jsonArray;
        inflater = LayoutInflater.from(context);
        this.avatar = avatar;

    }
    public  void setBackgroud(String background){
         this.background=background;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public JSONObject getItem(int position) {
        if (position == 0) {
            return null;
        } else {
            return data.get(position - 1);
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position == 0) {
            View view = inflater.inflate(R.layout.item_moments_header, null, false);
            ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            UserManager.get().loadUserAvatar(context, avatar, iv_avatar);
            iv_moment_bg = (ImageView) view.findViewById(R.id.iv_moment_bg);
            UserManager.get().loadImage(  context, background, iv_moment_bg,R.drawable.app_logo);

            view.setEnabled(false);
            return view;
        } else {
            convertView = inflater.inflate(R.layout.item_moments_me, parent, false);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.image_1 = (SimpleDraweeView) convertView
                        .findViewById(R.id.image_1);
                holder.tv_num = (TextView) convertView
                        .findViewById(R.id.tv_num);
                holder.tv_day = (TextView) convertView
                        .findViewById(R.id.tv_day);
                holder.tv_month = (TextView) convertView
                        .findViewById(R.id.tv_month);
                holder.tv_content = (TextView) convertView
                        .findViewById(R.id.tv_content);
                holder.tv_location = (TextView) convertView
                        .findViewById(R.id.tv_location);
                holder.view_header = (View) convertView
                        .findViewById(R.id.view_header);
                holder.image_video = (SimpleDraweeView) convertView
                        .findViewById(R.id.image_video);
                holder.rl_videoView=convertView.findViewById(R.id.rl_videoView);
                holder.tv_content_video=convertView.findViewById(R.id.tv_content_video);
                convertView.setTag(holder);
            }

            final JSONObject json = data.get(position - 1);
            // 如果数据出错....
            if (json == null || json.size() == 0) {
                data.remove(position - 1);
                this.notifyDataSetChanged();
            }
              String content = json.getString("content");
            String location = json.getString("location");
            String avatar = json.getString("avatar");
            String timeDesc = json.getString("timeDesc");
            // 设置文章中的图片
            if (json.containsKey("imgs")&&!TextUtils.isEmpty(json.getString("imgs"))) {
                String imageStr = json.getString("imgs");
                if (!TextUtils.isEmpty(imageStr)) {
                    String[] images = imageStr.split(",");
                    int imNumb = images.length;
                    holder.image_1.setVisibility(View.VISIBLE);
                    holder.image_1.setImageURI(Uri.parse(  images[0] + HTConstant.baseImgUrl_set));
                    holder.tv_num.setVisibility(View.VISIBLE);
                    holder.tv_num.setText(context.getString(R.string.total) + String.valueOf(imNumb) + context.getString(R.string.pieces));
                } else {
                    holder.image_1.setVisibility(View.GONE);
                    holder.tv_num.setVisibility(View.GONE);
                }
            }else {

                String videopath = json.getString("videos"); //TODO 屏蔽视频显示的
                if(!TextUtils.isEmpty(videopath)){
                 //   holder.image_1.setImageURI();
                     holder.tv_content.setVisibility(View.GONE);
                    holder.rl_videoView.setVisibility(View.VISIBLE);
                    holder.tv_content_video.setText(content);
                    holder.image_video.setImageURI(Uri.parse(videopath + HTConstant.baseVideoUrl_set));
                    holder.tv_num.setVisibility(View.GONE);
                    holder.tv_num.setText(context.getString(R.string.total) + String.valueOf(1) + context.getString(R.string.pieces));
                }else{
                    holder.rl_videoView.setVisibility(View.GONE);
                    holder.tv_num.setVisibility(View.GONE);
                    holder.tv_content.setVisibility(View.VISIBLE);
                }


            }
           holder.tv_day.setText(timeDesc);


           // String coverImage = json.getString("coverImage");
//            // 设置文章中的图片
//            if (json.containsKey("imagestr")) {
//                String imageStr = json.getString("imagestr");
//                if (!TextUtils.isEmpty(imageStr)) {
//                    String[] images = imageStr.split(",");
//                    int imNumb = images.length;
//                    holder.image_1.setVisibility(View.VISIBLE);
//                    holder.rl_videoView.setVisibility(View.GONE);
//                    holder.image_1.setImageURI(Uri.parse(HTConstant.baseImgUrl
//                            + images[0] + HTConstant.baseImgUrl_set));
//                    holder.tv_num.setVisibility(View.VISIBLE);
//                    holder.tv_num.setText(context.getString(R.string.total) + String.valueOf(imNumb) + context.getString(R.string.pieces));
//                } else {
//                    holder.image_1.setVisibility(View.GONE);
//                    holder.tv_num.setVisibility(View.GONE);
//                    holder.tv_content.setVisibility(View.GONE);
//                    if (!TextUtils.isEmpty(videopath) &&!TextUtils.isEmpty(coverImage)){
//                        holder.rl_videoView.setVisibility(View.VISIBLE);
//                        holder.tv_content_video.setText(content);
//                        holder.image_video.setImageURI(Uri.parse(HTConstant.baseImgUrl
//                                + coverImage + HTConstant.baseImgUrl_set));
//                        holder.tv_num.setVisibility(View.GONE);
//                        holder.tv_num.setText(context.getString(R.string.total) + String.valueOf(1) + context.getString(R.string.pieces));
//                    }else{
//                        holder.rl_videoView.setVisibility(View.GONE);
//                        holder.tv_num.setVisibility(View.GONE);
//                        holder.tv_content.setVisibility(View.VISIBLE);
//                    }
//                }
//            }else{
//                if (!TextUtils.isEmpty(videopath) &&!TextUtils.isEmpty(coverImage)){
//                    holder.tv_content.setVisibility(View.GONE);
//                    holder.rl_videoView.setVisibility(View.VISIBLE);
//                    holder.tv_content_video.setText(content);
//                    holder.image_video.setImageURI(Uri.parse(coverImage + HTConstant.baseImgUrl_set));
//                    holder.tv_num.setVisibility(View.GONE);
//                    holder.tv_num.setText(context.getString(R.string.total) + String.valueOf(1) + context.getString(R.string.pieces));
//                }else{
//                    holder.rl_videoView.setVisibility(View.GONE);
//                    holder.tv_num.setVisibility(View.GONE);
//                    holder.tv_content.setVisibility(View.VISIBLE);
//                }
//            }
            // 显示位置
            if (location != null && !location.equals("0")) {
                holder.tv_location.setVisibility(View.VISIBLE);
                holder.tv_location.setText(location);
            }
            // 显示文章内容
            holder.tv_content.setText(content);
            // 显示时间
          //  String serviceTime = DateUtils.getStringTime(System.currentTimeMillis());
//            if (serviceTimes != null && serviceTimes.size() > 0) {
//                serviceTime = serviceTimes.get(0);
//            }
           // final String finalServiceTime = serviceTime;
           // setDateText(rel_time, holder.tv_day, holder.tv_month, holder.view_header);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context,
                            MomentsDetailActivity.class).putExtra("data",
                            json.toJSONString()) );
                }
            });
            return convertView;
        }
    }


    public static class ViewHolder {
        // 时间
        TextView tv_num;
        SimpleDraweeView image_1, image_video;
        // 动态内容
        TextView tv_content;
        // 位置
        TextView tv_location;
        TextView tv_month;
        TextView tv_day, tv_content_video;
        // 顶部空格
        View view_header;
        RelativeLayout rl_videoView;
    }

    private void setDateText(String rel_time, TextView tv_day, TextView tv_month, View view_header) {
        String date = rel_time.substring(0, 10);
        String moth = rel_time.substring(5, 7);
        String day = rel_time.substring(8, 10);
        if (moth.startsWith("0")) {
            moth = moth.substring(1);
        }
        if (!date.equals(recordDate)) {
            view_header.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.VISIBLE);
            tv_month.setVisibility(View.VISIBLE);
            tv_day.setText(day);
            tv_month.setText(moth + context.getString(R.string.Moth));
        } else {
            view_header.setVisibility(View.GONE);
            tv_day.setVisibility(View.GONE);
            tv_month.setVisibility(View.GONE);
        }
    }
}
