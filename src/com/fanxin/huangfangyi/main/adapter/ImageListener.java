package com.fanxin.huangfangyi.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.fanxin.huangfangyi.main.moments.BigImageActivity;

/**
 * Created by huangfangyi on 2016/7/10.\
 * QQ:84543217
 */
public class ImageListener implements View.OnClickListener{
    private  String[] images;
    private   int page;
    private  Context context;
    public ImageListener(Context context,String[]images, int page) {

        this.images = images;
        this.page = page;
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(context, BigImageActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("page", page);
        context.startActivity(intent);

    }
}
