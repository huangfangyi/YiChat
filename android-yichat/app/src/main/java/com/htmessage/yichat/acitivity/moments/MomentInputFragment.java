package com.htmessage.yichat.acitivity.moments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.yichat.R;

/**
 * Created by slj on 2016/12/30.
 */

public class MomentInputFragment extends DialogFragment implements View.OnClickListener{
    TextView close;
    EditText editSendMessage;
    Button btnSend;
    RelativeLayout re_edittext;
    private static InputMethodManager imm;
    public MomentInputFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_moment_input, container, false);
        setLayout();
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        close.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initView(View view) {
        close = (TextView) view.findViewById(R.id.close);
        editSendMessage = (EditText) view.findViewById(R.id.edit_send_message);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        re_edittext = (RelativeLayout) view.findViewById(R.id.re_edittext);
    }

    public void setEditHint(Object msg) {
        if (editSendMessage!=null && msg!=null){
            if (msg instanceof Integer){
                editSendMessage.setHint((int)msg);
            }else if (msg instanceof String){
                editSendMessage.setHint((String)msg);
            }
        }
    }
    public void setEditText(Object msg) {
        if (editSendMessage!=null&&msg!=null){
            if (msg instanceof Integer){
                editSendMessage.setText((int)msg);
            }else if (msg instanceof String){
                editSendMessage.setText((String)msg);
            }
            editSendMessage.setSelection(editSendMessage.getText().length());
        }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                if (edittextListener != null) {
                    hideKeyboard();
                    this.dismiss();
                }
                break;
            case R.id.btn_send:
                String text = editSendMessage.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (edittextListener != null) {
                        edittextListener.setTextStr(text);
                        editSendMessage.setText("");
                        hideKeyboard();
                        this.dismiss();
                    }
                }
                break;
        }
    }
    public interface EdittextListener {
        void setTextStr(String text);

        void dismiss(DialogFragment dialogFragment);
    }

    private EdittextListener edittextListener;

    public void setEdittextListener(EdittextListener listener) {
        edittextListener = listener;
    }

    protected void setLayout() {
        Window window = getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(0));//背景透明
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height =ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(editSendMessage.getContext(), editSendMessage);
            }
        },150);
    }


    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) editSendMessage.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm!=null&&getDialog()!=null) {
                imm.hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (edittextListener != null) {
            edittextListener.dismiss(this);
        }
    }

    public  void hideKeyboard(Context context, View view) {
        try {
            view.requestFocus();
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm!=null) {
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
        }
    }

    public  void hideKeyboard(Activity activity) {
        try {
            imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm!=null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
        }
    }

    public  void showKeyboard(Context context, View view) {
        try {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm!=null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }catch (Exception e){
        }
    }
}
