package com.fanxin.huangfangyi.main.moments;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fanxin.huangfangyi.R;

public class AddPopWindow extends PopupWindow {
    private View conentView;
    private  Activity context;
    public AddPopWindow(final Activity context, ImageView iv_temp, final ClickCallBack clickCallBack) {
        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.fx_popwindow_moments, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        LinearLayout ll_zan = (LinearLayout) conentView
                .findViewById(R.id.ll_zan);
        LinearLayout ll_pl = (LinearLayout) conentView.findViewById(R.id.ll_pl);
        TextView tv_good = (TextView) conentView.findViewById(R.id.tv_good);
        tv_good.setText((String) iv_temp.getTag());
        ll_zan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // context.startActivity(new

                clickCallBack.clicked(1);

                AddPopWindow.this.dismiss();

            }

        });
        ll_pl.setOnClickListener(new OnClickListener() {
            // 扫一扫 ，调出扫二维码 gongfan
            @Override
            public void onClick(View v) {
                clickCallBack.clicked(2);

                AddPopWindow.this.dismiss();

            }

        });

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            conentView.measure(w, h);
            int height =conentView.getMeasuredHeight();
            int width =conentView.getMeasuredWidth();
            //
            // this.showAsDropDown(parent, 0, 0);
            // this.showAtLocation(parent, gravity, x, y);


            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, Gravity.NO_GRAVITY,
                    location[0]-width, location[1]);


        } else {
            this.dismiss();
        }
    }

    /**
     *
     *
     */
    public interface ClickCallBack {
        void clicked(int type);
    }
}
