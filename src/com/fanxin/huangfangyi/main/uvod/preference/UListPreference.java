package com.fanxin.huangfangyi.main.uvod.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.fanxin.huangfangyi.R;


public class UListPreference extends ListPreference {
    private CharSequence[] mEntrySummaries;

    public UListPreference(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private final void initPreference(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.UListPreference, 0, 0);
        if (a == null)
            return;

        mEntrySummaries = a.getTextArray(R.styleable.UListPreference_entrySummaries);

        a.recycle();
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        syncSummary();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        syncSummary();
    }

    @Override
    public void setValueIndex(int index) {
        super.setValueIndex(index);
        syncSummary();
    }

    public int getEntryIndex() {
        CharSequence[] entryValues = getEntryValues();
        CharSequence value = getValue();

        if (entryValues == null || value == null) {
            return -1;
        }

        for (int i = 0; i < entryValues.length; ++i) {
            if (TextUtils.equals(value, entryValues[i])) {
                return i;
            }
        }

        return -1;
    }

    public void setEntrySummaries(Context context, int resId) {
        setEntrySummaries(context.getResources().getTextArray(resId));
    }

    public void setEntrySummaries(CharSequence[] entrySummaries) {
        mEntrySummaries = entrySummaries;
        notifyChanged();
    }

    public CharSequence[] getEntrySummaries() {
        return mEntrySummaries;
    }

    private void syncSummary() {
        int index = getEntryIndex();
        if (index < 0)
            return;

        if (mEntrySummaries != null && index < mEntrySummaries.length) {
            setSummary(mEntrySummaries[index]);
        } else {
            setSummary(getEntries()[index]);
        }
    }
}
