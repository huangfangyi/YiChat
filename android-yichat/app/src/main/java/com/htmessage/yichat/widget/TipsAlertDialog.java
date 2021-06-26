package com.htmessage.yichat.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;

/**
 * 项目名称：KTZ
 * 类描述：TipsAlertDialog 描述:
 * 创建人：songlijie
 * 创建时间：2018/4/19 10:41
 * 邮箱:814326663@qq.com
 */
public class TipsAlertDialog extends Dialog implements View.OnClickListener {
    private TextView tv_tips_title, tv_tips_content, tv_cancle, tv_ok;
    private View view_line_dialog;
    private ImageView iv_tips_images;
    private String titleStr, okStr, cancleStr, contentMsg = null;
    private int showLineView = View.VISIBLE;
    private int showImageView = View.GONE;
    private int showOkButton = View.VISIBLE;
    private int showCancleButton = View.VISIBLE;
    private int showContent = View.VISIBLE;
    private int showTitle = View.VISIBLE;

    private OnTipsAlertClickListener listener;
    private Object imagePath = null;


    public TipsAlertDialog(Context context) {
        super(context,R.style.MyAlertDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tips_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        p.width = (int) (metrics.widthPixels * 0.9); // 宽度设置为屏幕的0.65，根据实际情况调整
        window.setAttributes(p);
        setCanceledOnTouchOutside(false);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        tv_cancle.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    private void initData() {
        view_line_dialog.setVisibility(showLineView == View.VISIBLE ? View.VISIBLE : showLineView);
        iv_tips_images.setVisibility(showImageView == View.GONE ? View.GONE : showImageView);
        tv_cancle.setVisibility(showCancleButton == View.VISIBLE ? View.VISIBLE : showCancleButton);
        tv_ok.setVisibility(showOkButton == View.GONE ? View.GONE : showOkButton);
        tv_tips_content.setVisibility(showContent == View.VISIBLE ? View.VISIBLE : showContent);
        tv_tips_title.setVisibility(showTitle == View.VISIBLE ? View.VISIBLE : showTitle);

        tv_tips_title.setText(TextUtils.isEmpty(titleStr) ? getContext().getString(R.string.prompt) : titleStr);
        tv_tips_content.setText(TextUtils.isEmpty(contentMsg) ? getContext().getString(R.string.prompt) : contentMsg);
        tv_cancle.setText(TextUtils.isEmpty(cancleStr) ? getContext().getString(R.string.cancel) : cancleStr);
        tv_ok.setText(TextUtils.isEmpty(okStr) ? getContext().getString(R.string.ok) : okStr);
        CommonUtils.loadImageCenterCrop(getContext(),imagePath,iv_tips_images);
    }

    private void initView() {
        tv_tips_title = (TextView) findViewById(R.id.tv_tips_title);
        tv_tips_content = (TextView) findViewById(R.id.tv_tips_content);
        iv_tips_images = (ImageView) findViewById(R.id.iv_tips_images);
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        view_line_dialog = findViewById(R.id.view_line_dialog);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.tv_ok:
                if (listener != null) {
                    listener.onTipsPromitClick();
                }
                break;
            case R.id.tv_cancle:
                if (listener != null) {
                    listener.onTipsCancleClick();
                }
                break;
        }
    }

    public interface OnTipsAlertClickListener {
        void onTipsPromitClick();

        void onTipsCancleClick();
    }

    /**
     * 设置按钮监听
     * @param lis
     */
    public void setOnTipsAlertClickListener(OnTipsAlertClickListener lis) {
        listener = lis;
    }

    /**
     * 设置标题的文字
     * @param title
     */
    public void setTipsTitle(Object title) {
        if (title instanceof Integer) {
            titleStr = getContext().getString(((int) title));
        } else if (title instanceof String) {
            titleStr = (String) title;
        }
    }

    /**
     * 设置中间的显示的文字
     * @param content
     */
    public void setTipsContent(Object content) {
        if (content instanceof Integer) {
            contentMsg = getContext().getString(((int) content));
        } else if (content instanceof String) {
            contentMsg = (String) content;
        }
    }

    /**
     * 设置确定按钮的文字
     * @param ok
     */
    public void setTipsOkStr(Object ok) {
        if (ok instanceof Integer) {
            okStr = getContext().getString(((int) ok));
        } else if (ok instanceof String) {
            okStr = (String) ok;
        }
    }

    /**
     * 设置取消按钮的文字
     * @param cancle
     */
    public void setTipsCancleStr(Object cancle) {
        if (cancle instanceof Integer) {
            cancleStr = getContext().getString(((int) cancle));
        } else if (cancle instanceof String) {
            cancleStr = (String) cancle;
        }
    }

    /**
     * 设置imageview是否显示 默认隐藏
     * @param visiblity
     */
    public void setImageViewVisiblity(int visiblity){
        showImageView = visiblity;
    }

    /**
     * 设置lineView是否显示 默认显示
     * @param visiblity
     */
    public void setLineViewVisiblity(int visiblity){
        showLineView = visiblity;
    }
    /**
     * 设置标题是否显示 默认显示
     * @param visiblity
     */
    public void setTitleVisiblity(int visiblity){
        showTitle = visiblity;
    }

    /**
     * 设置内容是否显示 默认显示
     * @param visiblity
     */
    public void setContentVisiblity(int visiblity){
        showContent = visiblity;
    }
    /**
     * 显示没有取消按钮
     * @param visiblity
     */
    public void setNoCancleButton(int visiblity){
        showCancleButton =visiblity;
        showLineView =visiblity;
    }
    /**
     * 显示没有确定按钮
     * @param visiblity
     */
    public void setNoOkButton(int visiblity){
        showOkButton =visiblity;
        showLineView =visiblity;
    }

    /**
     * 设置图片地址  当设置imageView为显示的时候可用
     * @param path
     */
    public void setImagePath(Object path){
        imagePath = path;
    }
    /**
     * 显示Tips
     */
    public void showTips(){
        this.show();
    }

    /**
     * 隐藏Tips
     */
    public void hintTips(){
        this.dismiss();
    }
}
