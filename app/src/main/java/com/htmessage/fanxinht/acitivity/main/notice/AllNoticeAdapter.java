package com.htmessage.fanxinht.acitivity.main.notice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：PersonalTailor
 * 类描述：AllNoticeAdapter 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 17:29
 * 邮箱:814326663@qq.com
 */
public class AllNoticeAdapter extends RecyclerView.Adapter<AllNoticeAdapter.ViewHolder> implements View.OnClickListener {
    private List<JSONObject> objects = new ArrayList<>();
    private Context mContext ;

    public AllNoticeAdapter(List<JSONObject> objects, Context mContext) {
        this.objects = objects;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_all_notice, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject jsonObject = objects.get(position);
        String title = jsonObject.getString("title");
        String time = jsonObject.getString("time");
        holder.tv_notice_title.setText(title);
        holder.tv_notice_time.setText(time);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(jsonObject);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public void onClick(View v) {
        if (listener !=null){
            listener.onItemClock(v, (JSONObject) v.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout rl_notice;
        private TextView tv_notice_title;
        private TextView tv_notice_time;
        private ImageView iv_notice;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_notice = (RelativeLayout) itemView.findViewById(R.id.rl_notice);
            tv_notice_title = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tv_notice_time = (TextView) itemView.findViewById(R.id.tv_notice_time);
            iv_notice = (ImageView) itemView.findViewById(R.id.iv_notice);
        }
    }
    private OnItemClockListener listener;

    public void setListener(OnItemClockListener listener) {
        this.listener = listener;
    }
    public  interface OnItemClockListener{
        void onItemClock(View view, JSONObject object);
    }
}
