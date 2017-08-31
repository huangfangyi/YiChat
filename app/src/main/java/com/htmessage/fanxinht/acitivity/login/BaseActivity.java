package com.htmessage.fanxinht.acitivity.login;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;

public class BaseActivity extends AppCompatActivity {
    public String TAG = this.getClass().getName().toString();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        HTApp.getInstance().saveActivity(this);
    }


    public void back(View view) {
        finish();
    }


    public void setTitle(int title) {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule();
//        imageView.setLayoutParams(layoutParams);


        if (textView != null) {
            textView.setText(title);
        }

    }

    public void setTitleCenter() {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
        textView.setGravity(Gravity.CENTER);
    }

    public void setTitle(String title) {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void hideBackView() {
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        View view = this.findViewById(R.id.view_temp);
        if (iv_back != null && view != null) {
            iv_back.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

    }

    public void changeBackView(int icon,View.OnClickListener onClickListener) {
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        View view = this.findViewById(R.id.view_temp);
        if (iv_back != null && view != null) {
            iv_back.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            iv_back.setImageResource(icon);
        }
        if (onClickListener != null) {
            iv_back.setOnClickListener(onClickListener);
        }
    }
    public void showRightView(int res, View.OnClickListener onClickListener) {
        ImageView ivRight = (ImageView) this.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setImageResource(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }

        }
    }

    public void showRightTextView(int res, View.OnClickListener onClickListener) {
        TextView ivRight = (TextView) this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setText(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightTextView(String res, View.OnClickListener onClickListener) {
        TextView ivRight = (TextView) this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setText(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    protected boolean isCompatible(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

}
