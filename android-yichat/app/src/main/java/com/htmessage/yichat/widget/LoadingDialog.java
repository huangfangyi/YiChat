package com.htmessage.yichat.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htmessage.yichat.R;

/**
 * 项目名称：KTZ
 * 类描述：LoadingDialog 描述:加载的dialog
 * 创建人：songlijie
 * 创建时间：2018/4/19 9:11
 * 邮箱:814326663@qq.com
 */
public class LoadingDialog extends Dialog implements View.OnClickListener {
    private LinearLayout dialog_view;
    private ProgressBar pb_loading;
    private TextView tv_loading_tips;
    private String titleMsg = null;
    private Drawable loadingDrawable = null;

    public LoadingDialog(Context context) {
        super(context, R.style.progress_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(false);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        dialog_view = (LinearLayout) this.findViewById(R.id.dialog_view);
        pb_loading = (ProgressBar) this.findViewById(R.id.pb_loading);
        tv_loading_tips = (TextView) this.findViewById(R.id.tv_loading_tips);
    }

    private void initData() {
        tv_loading_tips.setText(TextUtils.isEmpty(titleMsg) ? this.getContext().getString(R.string.loading) : titleMsg);
        pb_loading.setIndeterminateDrawable(loadingDrawable == null ? this.getContext().getDrawable(R.drawable.progress_drawable_white) : loadingDrawable);
    }

    private void setListener() {
        dialog_view.setOnClickListener(this);
    }

    public void setLoadingText(Object msg) {
        if (msg instanceof Integer) {
            titleMsg = getContext().getString(((int) msg));
        } else if (msg instanceof String) {
            titleMsg = (String) msg;
        }
    }

    /**
     * 设置6.0以上的加载动画
     *
     * @param drawable
     */
    public void setLoadV23Animin(Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            loadingDrawable = drawable;
        }
    }

    /**
     * 设置加载动画
     *
     * @param drawable
     */
    public void setLoadAnimin(Drawable drawable) {
        loadingDrawable = drawable;
    }

    /**
     * 设置默认的加载动画
     */
    public void setLoadAnimin() {
        loadingDrawable = this.getContext().getDrawable(R.drawable.progress_drawable_white);
    }

    /**
     * 设置默认的6.0以上的clip加载动画
     */
    public void setLoadV23Animin() {
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            loadingDrawable = this.getContext().getDrawable(R.drawable.progress_drawable_white_v23);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_view:
                this.dismiss();
                break;
        }
    }
}
