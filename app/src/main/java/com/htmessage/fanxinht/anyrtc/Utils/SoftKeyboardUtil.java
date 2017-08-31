package com.htmessage.fanxinht.anyrtc.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * Created by xiongxuesong-pc on 2016/6/14.
 * 移动控件使得键盘不会挤压整体布局
 */
public class SoftKeyboardUtil {

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private InputMethodManager imm;
    int vHeight = 0;

    public SoftKeyboardUtil() {

    }

    /**
     * 监听键盘高度和键盘时候处于打开状态，在调用的Activity中的onDestroy()方法中调用
     * 该类中的removeGlobalOnLayoutListener()方法来移除监听
     *
     * @param activity
     * @param listener
     */
    public void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        final int statusBarHeight = getStatusBarHeight(activity);// 状态栏的高度
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int keyboardHeight;// 软键盘的高度
            private boolean isShowKeyboard;// 软键盘的显示状态
            private boolean isVKeyMap;//虚拟按键，华为手机

            @Override
            public void onGlobalLayout() {
                onGlobalLayoutListener = this;

                // 应用可以显示的区域。此处包括应用占用的区域，
                // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                // 屏幕高度。这个高度不含虚拟按键的高度
                int screenHeight = decorView.getRootView().getHeight();


                // 在不显示软键盘时，heightDiff等于状态栏的高度
                // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
                // 所以heightDiff大于状态栏高度时表示软键盘出现了，
                // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度


                if ((screenHeight - r.bottom) < screenHeight / 4) {
                    isVKeyMap = true;
                    vHeight = screenHeight - r.bottom;
                } else if ((screenHeight - r.bottom) == 0) {
                    vHeight = 0;
                    isVKeyMap = false;
                }

                int heightDiff = screenHeight - (r.bottom - r.top);
                if (keyboardHeight == 0 && heightDiff > (screenHeight / 4)) {
                    if (isVKeyMap) {
                        keyboardHeight = heightDiff - vHeight;
                    } else {
                        keyboardHeight = heightDiff;
                    }
                }

                if (isVKeyMap) {

                    if (isShowKeyboard) {
                        // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                        // 说明这时软键盘已经收起
                        if (heightDiff <= (statusBarHeight + vHeight)) {
                            isShowKeyboard = false;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    } else {
                        // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                        if (heightDiff > (statusBarHeight) && heightDiff > (screenHeight / 4)) {
                            isShowKeyboard = true;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    }
                } else {
                    if (isShowKeyboard) {
                        // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                        // 说明这时软键盘已经收起
                        if (heightDiff <= statusBarHeight) {
                            isShowKeyboard = false;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    } else {
                        if (heightDiff > (statusBarHeight)) {
                            isShowKeyboard = true;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    }
                }


            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow);
    }

    public void removeGlobalOnLayoutListener(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (onGlobalLayoutListener != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                decorView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            } else {
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    // 获取状态栏高度
    public int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void hideKeyboard(Context context, View view) {
        view.requestFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void hideKeyboard(Activity activity) {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(Context context, View view) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
}