package com.fanxin.huangfangyi.main.moments;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LinkifyUtil {   
    private final Activity currentActivity;   
  
    public LinkifyUtil(Activity activity) {   
        this.currentActivity = activity;   
    }   
  
    public void addIntentLink(final Intent intent, final TextView view, final int start, final int end) {   
        CharSequence source = view.getText();   
        if (source instanceof Spanned) {   
            IntentSpan[] spans = ((Spanned) source).getSpans(start, end, IntentSpan.class);   
            if (spans.length > 0) {   
                return;   
            }   
        }   
           
        SpannableString spannableString = new SpannableString(source);   
        spannableString.setSpan(new IntentSpan(new OnClickListener() {   
  
            public void onClick(View view) {   
                currentActivity.startActivity(intent);   
            }   
        }), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
           
        view.setText(spannableString);   
        view.setMovementMethod(LinkMovementMethod.getInstance());   
    }   
  
    public void addIntentLink(final Intent intent, final TextView view) {   
        addIntentLink(intent, view, 0, view.getText().toString().length());   
    }   
}  