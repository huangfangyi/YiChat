package com.htmessage.yichat.acitivity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.yichat.utils.CommonUtils;


public class BaseFragmentActivity extends FragmentActivity {
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

    public void changeBackView(int icon, View.OnClickListener onClickListener) {
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
    public void showRightView(int res, View.OnClickListener onClickListener,View.OnLongClickListener onLongClickListener) {
        ImageView ivRight = (ImageView) this.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setImageResource(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
            if (onLongClickListener != null) {
                ivRight.setOnLongClickListener(onLongClickListener);
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

    public void hintRightTextView() {
        TextView ivRight = (TextView) this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setVisibility(View.GONE);
        }
    }
    public void hintRightImageView() {
        ImageView ivRight = (ImageView) this.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setVisibility(View.GONE);
        }
    }
    protected boolean isCompatible(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }



    @Override
    protected void onResume() {
        super.onResume();
        HTClient.getInstance().refreshIMConnection();

    }

    @Override
    protected void onDestroy() {

        HTApp.getInstance().removeActivity(this);
        CommonUtils.cancelDialogOnDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 只要发生onSaveInstanceState就remove all Fragment
        if(outState!=null){
            outState.remove("android:support:fragments");
        }
    }

}
