package com.htmessage.fanxinht.anyrtc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.anyrtc.Config.LiveItemBean;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

public class LiveHosterAdapter extends BGARecyclerViewAdapter<LiveItemBean> {
    private Context mContext;
    private RecyclerView recyclerView;

    public LiveHosterAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView, R.layout.live_item);
        mContext = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
        viewHolderHelper.setItemChildClickListener(R.id.rlayout_item);
    }

    @Override
    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int i, LiveItemBean livesBean) {
        if (livesBean != null) {
            recyclerView.setBackgroundResource(R.color.white);
            bgaViewHolderHelper.setText(R.id.txt_live_name, mContext.getString(R.string.theme_input) + livesBean.getmLiveTopic());
            if (livesBean.getmMemNumber() != 0) {
                bgaViewHolderHelper.setText(R.id.txt_live_number, mContext.getString(R.string.peoples) + livesBean.getmMemNumber());
            } else {
                bgaViewHolderHelper.setText(R.id.txt_live_number, "");
            }

        } else {
            recyclerView.setBackgroundResource(R.drawable.has_no_msg);
        }
    }
}