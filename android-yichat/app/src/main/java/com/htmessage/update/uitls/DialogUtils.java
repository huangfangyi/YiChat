package com.htmessage.update.uitls;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htmessage.yichat.R;

/**
 * Created by huangfangyi on 2019/7/27.
 * qq 84543217
 */
public class DialogUtils {

    /**
     * 发起弹窗
     *
     * @param context
     * @param loadText
     */
    public static Dialog creatDialog(Context context, Object loadText) {

        Dialog progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ProgressBar progressBarWaiting = (ProgressBar) progressDialog.findViewById(R.id.iv_loading);
        TextView tv_loading_text = (TextView) progressDialog.findViewById(R.id.tv_loading_text);
        if (loadText instanceof Integer) {
            tv_loading_text.setText(((int) loadText));
        } else if (loadText instanceof String) {
            tv_loading_text.setText((String) loadText);
        }
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            final Drawable drawable = context.getDrawable(R.drawable.progress_drawable_white_v23);
            progressBarWaiting.setIndeterminateDrawable(drawable);
        }
        return progressDialog;
    }

}
