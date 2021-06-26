package com.htmessage.yichat.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.htmessage.yichat.R;


public class SwitchButton extends FrameLayout{

    private ImageView openImage;
    private ImageView closeImage;

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public SwitchButton(Context context) {
        this(context, null);
    }
    
    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        Drawable openDrawable = ta.getDrawable(R.styleable.SwitchButton_switchOpenImage);
        Drawable closeDrawable = ta.getDrawable(R.styleable.SwitchButton_switchCloseImage);
        int switchStatus = ta.getInt(R.styleable.SwitchButton_switchStatus, 0);
        ta.recycle();
        
        LayoutInflater.from(context).inflate(R.layout.widget_switch_button, this);
        openImage = (ImageView) findViewById(R.id.iv_switch_open);
        closeImage = (ImageView) findViewById(R.id.iv_switch_close);
        if(openDrawable != null){
            openImage.setImageDrawable(openDrawable);
        }
        if(closeDrawable != null){
            closeImage.setImageDrawable(closeDrawable);
        }
        if(switchStatus == 1){
            closeSwitch();
        }
        
    }
    
    /**
     * is switch open
     */
    public boolean isSwitchOpen(){
        return openImage.getVisibility() == View.VISIBLE;
    }

    public void openSwitch(){
    	openImage.setVisibility(View.VISIBLE);
    	closeImage.setVisibility(View.INVISIBLE);
    }
    
    public void closeSwitch(){
    	openImage.setVisibility(View.INVISIBLE);
    	closeImage.setVisibility(View.VISIBLE);
    }
    
}
