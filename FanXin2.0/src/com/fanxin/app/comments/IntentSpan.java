package com.fanxin.app.comments;

import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class IntentSpan extends ClickableSpan {   
	  
    private final OnClickListener listener;   
  
    public IntentSpan(View.OnClickListener listener) {   
        this.listener = listener;   
    }   
  
    @Override  
    public void onClick(View view) {   
        listener.onClick(view);   
    }   
}  