package com.htmessage.fanxinht.anyrtc.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ScrollRecycerView extends RecyclerView {


    String TAG = this.getClass().getSimpleName();
    private boolean isScrollingToBottom = true;
    private boolean isShowBottom = false; // 是否在底部
    private ViewGroup floatingActionButton;
    private ScrollPosation listener;


    public ScrollRecycerView(Context context) {
        super(context);
    }

    public ScrollRecycerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollRecycerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addScrollPosation(ScrollPosation loadMoreListener) {
        this.listener = loadMoreListener;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        isScrollingToBottom = dy > 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            int totalItemCount = layoutManager.getItemCount();
            if (lastVisibleItem == (totalItemCount - 1) && isScrollingToBottom) {
                isShowBottom = true;
                if (listener != null)
                    listener.ScrollButtom();
            } else {
                isShowBottom = false;
            }
        }
    }


    public interface ScrollPosation {
        void ScrollButtom();

        void ScrollNotButtom();
    }


    /**
     * 判读是否在底部
     *
     * @return
     */
    public boolean isShowBottom() {
        return isShowBottom;
    }


}
