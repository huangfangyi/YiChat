package com.fanxin.huangfangyi.main.moments;

import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class IntentSpan extends ClickableSpan {   
	  
    private final OnClickListener listener;   
  
    public IntentSpan(OnClickListener listener) {
        this.listener = listener;   
    }   
  
    @Override  
    public void onClick(View view) {   
        listener.onClick(view);   
    }   
}  