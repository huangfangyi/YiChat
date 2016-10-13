package com.fanxin.huangfangyi.main.ulive.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by leewen on 2015/10/23.
 */
public class UEditTextPreference extends EditTextPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public UEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        syncSumary();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        syncSumary();
    }

    private void syncSumary() {
        setSummary(getText().toString());
    }

}
