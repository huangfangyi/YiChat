package com.htmessage.yichat.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 项目名称：OcTalk
 * 类描述：RecyclerViewItemDecorntion 描述:
 * 创建人：songlijie
 * 创建时间：2018/1/5 13:15
 * 邮箱:814326663@qq.com
 */
public class RecyclerViewItemDecorntion extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0,5,0,5);
    }
}
